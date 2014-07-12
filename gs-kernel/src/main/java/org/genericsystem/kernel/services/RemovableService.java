package org.genericsystem.kernel.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.genericsystem.kernel.exceptions.AliveConstraintViolationException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.ReferentialIntegrityConstraintViolationException;

public interface RemovableService<T extends RemovableService<T>> extends BindingService<T> {

	boolean isCascadeRemove(int pos);

	@Override
	boolean isReferentialIntegrityConstraintEnabled(int pos);

	default void remove(RemoveStrategy removeStrategy) {
		switch (removeStrategy) {
		case NORMAL:
			remove();
			break;
		case FORCE:
			forceRemove();
			break;
		}
	}

	default void remove() {
		try {
			getOrderedDependenciesToRemove().forEach(this::simpleRemove);
		} catch (ConstraintViolationException e) {
			rollbackAndThrowException(e);
		}
	}

	default Iterable<T> getOrderedDependenciesToRemove() throws ConstraintViolationException {
		return reverseLinkedHashSet(buildOrderedDependenciesToRemove());
	}

	default LinkedHashSet<T> buildOrderedDependenciesToRemove() throws ReferentialIntegrityConstraintViolationException {
		@SuppressWarnings("unchecked")
		T restructoratorService = (T) this;
		return new LinkedHashSet<T>() {
			private static final long serialVersionUID = -3610035019789480505L;
			{
				visit(restructoratorService);
			}

			public void visit(T generic) throws ReferentialIntegrityConstraintViolationException {
				if (add(generic)) {// protect from loop
					if (!generic.getInheritings().isEmpty() || !generic.getInstances().isEmpty())
						throw new ReferentialIntegrityConstraintViolationException("Ancestor : " + generic + " has an inheritance or instance dependency");

					for (T composite : generic.getComposites())
						if (!generic.equals(composite)) {
							for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
								if (!/* compositeDependency.isAutomatic() && */composite.getComponents().get(componentPos).equals(generic) && !contains(composite) && composite.isReferentialIntegrityConstraintEnabled(componentPos))
									throw new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + generic + " by component position : " + componentPos);
							visit(composite);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (generic.isCascadeRemove(axe))
							visit(generic.getComponents().get(axe));
				}
			}
		};
	}

	default Iterable<T> reverseLinkedHashSet(LinkedHashSet<T> linkedHashSet) {
		List<T> dependencies = new ArrayList<T>(linkedHashSet);
		Collections.reverse(dependencies);
		return dependencies;
	}

	default void simpleRemove(T vertex) {
		if (!vertex.isAlive())
			rollbackAndThrowException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		if (!vertex.getInstances().isEmpty() || !vertex.getInheritings().isEmpty() || !vertex.getComposites().isEmpty())
			rollbackAndThrowException(new IllegalStateException(vertex.info() + " has dependencies"));
		/* if (!(automatics.remove(vertex) || adds.remove(vertex))) removes.add(vertex); */
		vertex.unplug();
	}

	default void forceRemove() {
		computeAllDependencies().forEach(x -> simpleRemove(x));
	}

	public enum RemoveStrategy {
		NORMAL, FORCE;
	}

}
