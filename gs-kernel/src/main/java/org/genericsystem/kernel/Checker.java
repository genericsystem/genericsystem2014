package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.CrossEnginesAssignementsException;
import org.genericsystem.api.exception.GetInstanceConstraintViolationException;
import org.genericsystem.api.exception.MetaLevelConstraintViolationException;
import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.genericsystem.api.exception.NotAliveConstraintViolationException;
import org.genericsystem.api.exception.NotAllowedSerializableTypeException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.annotations.Priority;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;

public class Checker<T extends AbstractVertex<T>> {

	private final Context<T> context;

	public Checker(Context<T> context) {
		this.context = context;
	}

	public Context<T> getContext() {
		return context;
	}

	public void check(boolean isOnAdd, boolean isFlushTime, T vertex) throws RollbackException {
		checkSystemConstraints(isOnAdd, isFlushTime, vertex);
		checkConsistency(vertex);
		checkConstraints(isOnAdd, isFlushTime, vertex);
	}

	void checkIsAlive(T vertex) {
		if (!context.isAlive(vertex))
			context.discardWithException(new AliveConstraintViolationException(vertex.info()));
	}

	void checkIsNotAlive(T vertex) {
		if (context.isAlive(vertex))
			context.discardWithException(new NotAliveConstraintViolationException(vertex.info()));
	}

	protected void checkSystemConstraints(boolean isOnAdd, boolean isFlushTime, T vertex) {
		checkWellFormedMeta(vertex);
		if (isOnAdd || !isFlushTime)
			checkIsAlive(vertex);
		else
			checkIsNotAlive(vertex);
		if (!isOnAdd)
			checkDependenciesAreEmpty(vertex);
		checkSerializableType(vertex);
		checkSameEngine(vertex);
		checkDependsMetaComponents(vertex);
		checkSupers(vertex);
		checkDependsSuperComponents(vertex);
		checkLevel(vertex);
		checkLevelComponents(vertex);
		checkGetInstance(vertex);
	}

	private void checkSerializableType(T vertex) {
		Serializable value = vertex.getValue();
		if (value != null) {
			if (value instanceof org.genericsystem.kernel.systemproperty.AxedPropertyClass)
				return;
			if (value instanceof Class)
				return;
			if (value instanceof String)
				return;
			if (value instanceof Integer)
				return;
			if (value instanceof Boolean)
				return;
			if (value instanceof byte[])
				return;
			if (value instanceof Double)
				return;
			if (value instanceof Float)
				return;
			if (value instanceof Short)
				return;
			if (value instanceof Long)
				return;
			context.discardWithException(new NotAllowedSerializableTypeException("Not allowed type for your serializable. Only primitive and Byte[] allowed."));
		}

	}

	private void checkSameEngine(T vertex) {
		DefaultRoot<T> root = vertex.getRoot();
		for (T component : vertex.getComponents())
			if (component != null && !root.equals(component.getRoot()))
				context.discardWithException(new CrossEnginesAssignementsException("Unable to associate " + vertex + " with his component " + component + " because they are from differents engines"));
		for (T directSuper : vertex.getSupers())
			if (directSuper != null && !root.equals(directSuper.getRoot()))
				context.discardWithException(new CrossEnginesAssignementsException("Unable to associate " + vertex + " with his super " + directSuper + " because they are from differents engines"));
	}

	private void checkWellFormedMeta(T vertex) {
		if (vertex.isMeta())
			if (!vertex.getComponents().stream().allMatch(c -> c.isRoot()) || !Objects.equals(vertex.getValue(), context.getRoot().getValue()) || vertex.getSupers().size() != 1 || !vertex.getSupers().get(0).isMeta())
				context.discardWithException(new IllegalStateException("Malformed meta : " + vertex.info()));
	}

	private void checkDependenciesAreEmpty(T vertex) {
		if (!context.getInstances(vertex).isEmpty() || !context.getInheritings(vertex).isEmpty() || !context.getComposites(vertex).isEmpty())
			context.discardWithException(new ReferentialIntegrityConstraintViolationException("Unable to remove : " + vertex.info() + " cause it has dependencies"));
	}

	private void checkDependsMetaComponents(T vertex) {
		if (vertex.getMeta().getComponents().size() != vertex.getComponents().size())
			context.discardWithException(new MetaRuleConstraintViolationException("Added generic and its meta do not have the same components size. Added node components : " + vertex.getComponents() + " and meta components : "
					+ vertex.getMeta().getComponents()));
		for (int pos = 0; pos < vertex.getComponents().size(); pos++) {
			T component = vertex.getComponent(pos);
			T metaComponent = vertex.getMeta().getComponent(pos);
			if (component == null)
				if (metaComponent == null)
					continue;
				else
					component = vertex;
			else if (metaComponent == null)
				metaComponent = vertex.getMeta();
			if (!component.isInstanceOf(metaComponent) && !component.inheritsFrom(metaComponent))
				context.discardWithException(new MetaRuleConstraintViolationException("Component of added generic : " + component + " must be instance of or must inherits from the component of its meta : " + metaComponent));
		}
	}

	private void checkLevelComponents(T vertex) {
		for (T component : vertex.getComponents())
			if ((component == null ? vertex.getLevel() : component.getLevel()) > vertex.getLevel())
				context.discardWithException(new MetaLevelConstraintViolationException("Inappropriate component meta level : " + component.getLevel() + " for component : " + component + ". Component meta level for added node is : " + vertex.getLevel()));
	}

	private void checkLevel(T vertex) {
		if (vertex.getLevel() > Statics.CONCRETE)
			context.discardWithException(new MetaLevelConstraintViolationException("Unable to instanciate a concrete generic : " + vertex.getMeta()));
	}

	private void checkSupers(T vertex) {
		vertex.getSupers().forEach(x -> checkIsAlive(x));
		if (!vertex.getSupers().stream().allMatch(superVertex -> superVertex.getLevel() == vertex.getLevel()))
			context.discardWithException(new IllegalStateException("Inconsistant supers (bad level) : " + vertex.getSupers()));
		if (!vertex.getSupers().stream().allMatch(superVertex -> vertex.getMeta().inheritsFrom(superVertex.getMeta())))
			context.discardWithException(new IllegalStateException("Inconsistant supers : " + vertex.getSupers()));
		if (!vertex.getSupers().stream().noneMatch(this::equals))
			context.discardWithException(new IllegalStateException("Supers loop detected : " + vertex.info()));
		if (vertex.getSupers().stream().anyMatch(superVertex -> Objects.equals(superVertex.getValue(), vertex.getValue()) && superVertex.getComponents().equals(vertex.getComponents()) && vertex.getMeta().inheritsFrom(superVertex.getMeta())))
			context.discardWithException(new IllegalStateException("Collision detected : " + vertex.info()));
	}

	private void checkDependsSuperComponents(T vertex) {
		vertex.getSupers().forEach(superVertex -> {
			if (!superVertex.isSuperOf(vertex.getMeta(), vertex.getSupers(), vertex.getValue(), vertex.getComponents()))
				context.discardWithException(new IllegalStateException("Inconsistant components : " + vertex.getComponents()));
		});
	}

	private void checkGetInstance(T vertex) {
		if (context.getInstances(vertex.getMeta()).get().filter(x -> ((AbstractVertex<?>) x).equalsRegardlessSupers(vertex.getMeta(), vertex.getValue(), vertex.getComponents())).count() > 1)
			context.discardWithException(new GetInstanceConstraintViolationException("get too many result for search : " + vertex.info()));
	}

	private void checkConstraints(boolean isOnAdd, boolean isFlushTime, T vertex) {
		if (vertex.getMap() != null) {
			Stream<T> contraintsHolders = vertex.getMeta().getHolders(vertex.getMap()).get()
					.filter(holder -> holder.getMeta().getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) holder.getMeta().getValue()).getClazz()))
					.filter(holder -> holder.getValue() != null && !Boolean.FALSE.equals(holder.getValue())).sorted(CONSTRAINT_PRIORITY);
			contraintsHolders.forEach(constraintHolder -> {
				T baseComponent = constraintHolder.getBaseComponent();
				if (vertex.isSpecializationOf(baseComponent))
					check(constraintHolder, baseComponent, isFlushTime, isOnAdd, false, vertex);
				T targetComponent = constraintHolder.getTargetComponent();
				if (targetComponent != null && vertex.isSpecializationOf(targetComponent))
					check(constraintHolder, baseComponent, isFlushTime, isOnAdd, true, vertex);
			});
		}
	}

	private void check(T constraintHolder, T baseComponent, boolean isFlushTime, boolean isOnAdd, boolean isRevert, T vertex) {
		try {
			statelessConstraint(constraintHolder.getMeta()).check(vertex, baseComponent, constraintHolder.getValue(), ((AxedPropertyClass) constraintHolder.getMeta().getValue()).getAxe(), isOnAdd, isFlushTime, isRevert);
		} catch (ConstraintViolationException e) {
			context.discardWithException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Constraint<T> statelessConstraint(T vertex) {
		try {
			return (Constraint<T>) ((AxedPropertyClass) vertex.getValue()).getClazz().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			context.discardWithException(e);
		}
		return null;
	}

	private int getConstraintPriority(T vertex) {
		Class<?> clazz = ((AxedPropertyClass) vertex.getValue()).getClazz();
		Priority priority = clazz.getAnnotation(Priority.class);
		return priority != null ? priority.value() : 0;
	}

	private void checkConsistency(T vertex) {
		if (vertex.getMap() != null && vertex.getMeta().getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) vertex.getMeta().getValue()).getClazz()) && vertex.getValue() != null
				&& !Boolean.FALSE.equals(vertex.getValue())) {
			T baseConstraint = vertex.getComponent(Statics.BASE_POSITION);
			int axe = ((AxedPropertyClass) vertex.getMeta().getValue()).getAxe();
			if (((AxedPropertyClass) vertex.getMeta().getValue()).getAxe() == Statics.NO_POSITION)
				baseConstraint.getAllInstances().forEach(x -> check(vertex, baseConstraint, true, true, false, x));
			else
				baseConstraint.getComponents().get(axe).getAllInstances().forEach(x -> check(vertex, baseConstraint, true, true, true, x));
		}
	}

	private final Comparator<T> CONSTRAINT_PRIORITY = new Comparator<T>() {
		@Override
		public int compare(T constraintHolder, T compareConstraintHolder) {
			return getConstraintPriority(constraintHolder.getMeta()) < getConstraintPriority(compareConstraintHolder.getMeta()) ? -1 : 1;
		}
	};

}
