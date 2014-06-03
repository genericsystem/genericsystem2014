package org.genericsystem.kernel.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.genericsystem.kernel.RemoveRestructurator;
import org.genericsystem.kernel.exceptions.AliveConstraintViolationException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.statics.RemoveStrategy;

public interface Removable<T extends Removable<T>> extends BindingService<T> {

	@SuppressWarnings("unchecked")
	default void remove(RemoveStrategy removeStrategy) {
		switch (removeStrategy) {
		case NORMAL:
			removeInstance();
			break;
		case FORCE:
			removeCascade();
			break;
		case CONSERVE:
			new RemoveRestructurator<T>((T) Removable.this) {
				private static final long serialVersionUID = 6513791665544090616L;
			}.rebuildAll();
			break;
		}
	}

	default void removeInstance() {
		try {
			for (T vertex : getOrderedDependenciesToRemove())
				simpleRemove(vertex);
		} catch (ConstraintViolationException e) {
			rollbackAndThrowException(e);
		}
	}

	default Iterable<T> getOrderedDependenciesToRemove() throws ConstraintViolationException {
		List<T> dependencies = new ArrayList<T>(buildOrderedDependenciesToRemove());
		Collections.reverse(dependencies);
		return dependencies;
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

	default void simpleRemove(T vertex) throws AliveConstraintViolationException {
		if (!vertex.isAlive())
			rollbackAndThrowException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		if (!vertex.getInstances().isEmpty() || !vertex.getInheritings().isEmpty() || !vertex.getComposites().isEmpty())
			rollbackAndThrowException(new IllegalStateException(vertex.info() + " has dependencies"));
		/* if (!(automatics.remove(vertex) || adds.remove(vertex))) removes.add(vertex); */
		vertex.unplug();
	}

	default void removeCascade() {
		computeAllDependencies().forEach(x -> x.unplug());
	}

}
