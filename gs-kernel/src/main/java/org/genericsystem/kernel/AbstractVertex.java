package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Statics.Supers;
import org.genericsystem.kernel.exceptions.AliveConstraintViolationException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.ReferentialIntegrityConstraintViolationException;

public abstract class AbstractVertex<T extends AbstractVertex<T>> extends Signature<T> implements VertexService<T> {

	protected List<T> supers;

	@Override
	@SuppressWarnings("unchecked")
	public T init(T meta, List<T> supers, Serializable value, List<T> components) {
		super.init(meta, value, components);
		this.supers = supers;
		checkDependsMetaComponents();
		checkSupers(supers);
		checkDependsSuperComponents(supers);
		return (T) this;
	}

	private void checkDependsMetaComponents() {
		if (getValue().toString().equals(SystemMap.class.toString()) || getValue().toString().contains("PropertyConstraint"))
			return;
		assert getMeta().getComponents() != null;
		if (!(getMeta().componentsDepends(getComponents(), getMeta().getComponents())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponents() + " " + getMeta().getComponents()));
	}

	private void checkSupers(List<T> supers) {
		supers.forEach(Signature::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupers()));
		if (!supers.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupers()));
		if (!supers.stream().noneMatch(this::equals))
			rollbackAndThrowException(new IllegalStateException("Supers loop detected : " + this.info()));
	}

	private void checkDependsSuperComponents(List<T> supers) {
		getSupersStream().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), supers, getValue(), getComponents()))
				rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}

	@Override
	public List<T> getSupers() {
		return supers;
	}

	@SuppressWarnings("static-method")
	<U> Dependencies<U> buildDependencies() {
		return new DependenciesImpl<U>();
	}

	public abstract T plug();

	public abstract boolean unplug();

	protected void forceRemove() {
		computeAllDependencies().forEach(this::simpleRemove);
	}

	void simpleRemove(T vertex) {
		if (!vertex.isAlive())
			rollbackAndThrowException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		if (!vertex.getInstances().isEmpty() || !vertex.getInheritings().isEmpty() || !vertex.getComposites().isEmpty())
			rollbackAndThrowException(new IllegalStateException(vertex.info() + " has dependencies"));
		/* if (!(automatics.remove(vertex) || adds.remove(vertex))) removes.add(vertex); */
		vertex.unplug();
	}

	private LinkedHashSet<T> buildOrderedDependenciesToRemove() throws ReferentialIntegrityConstraintViolationException {
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

	private Iterable<T> reverseLinkedHashSet(LinkedHashSet<T> linkedHashSet) {
		List<T> dependencies = new ArrayList<T>(linkedHashSet);
		Collections.reverse(dependencies);
		return dependencies;
	}

	private Iterable<T> getOrderedDependenciesToRemove() throws ConstraintViolationException {
		return reverseLinkedHashSet(buildOrderedDependenciesToRemove());
	}

	@Override
	public void remove() {
		try {
			getOrderedDependenciesToRemove().forEach(x -> this.simpleRemove(x));
		} catch (ConstraintViolationException e) {
			rollbackAndThrowException(e);
		}
	}

	@Override
	public T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents) {
		if (newComponents.size() != getComponents().size())
			rollbackAndThrowException(new IllegalArgumentException());
		return rebuildAll(() -> newT().init(getMeta(), new Supers<T>(getSupers(), supersToAdd), newValue, newComponents).plug());
	}

	// should not be public !!!
	@SuppressWarnings("unchecked")
	T rebuildAll(Supplier<T> rebuilder) {
		class ConvertMap extends HashMap<T, T> {
			private static final long serialVersionUID = 5003546962293036021L;

			T convert(T dependency) {
				if (dependency.isAlive())
					return dependency;
				T newDependency = get(dependency);
				if (newDependency == null) {
					T meta = (dependency.isRoot()) ? dependency : convert(dependency.getMeta());
					newDependency = meta.buildInstance(dependency.getSupersStream().map(x -> convert(x)).collect(Collectors.toList()), dependency.getValue(),
							dependency.getComponentsStream().map(x -> x.equals(this) ? null : convert(x)).collect(Collectors.toList())).plug();
					put(dependency, newDependency);
				}
				return newDependency;
			}
		}
		ConvertMap convertMap = new ConvertMap();
		LinkedHashSet<T> dependenciesToRebuild = computeAllDependencies();
		dependenciesToRebuild.forEach(this::simpleRemove);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(this);
		convertMap.put((T) this, build);
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));
		return build;
	}

	T rebuildAll(Supplier<T> rebuilder, List<T> overrides, Serializable value, List<T> components) {
		class ConvertMap extends HashMap<T, T> {
			private static final long serialVersionUID = 5003546962293036021L;

			T convert(T dependency) {
				if (dependency.isAlive())
					return dependency;
				T newDependency = get(dependency);
				if (newDependency == null) {
					T meta = (dependency.equals(dependency.getMeta())) ? dependency : convert(dependency.getMeta());
					newDependency = meta.buildInstance(dependency.getSupersStream().map(x -> convert(x)).collect(Collectors.toList()), dependency.getValue(),
							dependency.getComponentsStream().map(x -> x.equals(this) ? null : convert(x)).collect(Collectors.toList())).plug();
					put(dependency, newDependency);
				}
				return newDependency;
			}
		}
		ConvertMap convertMap = new ConvertMap();
		LinkedHashSet<T> dependenciesToRebuild = computeAllPotentialInstancesDependencies(overrides, value, components);
		dependenciesToRebuild.forEach(this::simpleRemove);

		T build = rebuilder.get();
		convertMap.put((T) this, build);

		dependenciesToRebuild.forEach(x -> convertMap.convert(x));

		return build;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T addInstance(List<T> overrides, Serializable value, T... components) {
		checkSameEngine(Arrays.asList(components));
		checkSameEngine(overrides);
		T nearestMeta = adjustMeta(overrides, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.addInstance(overrides, value, components);
		T weakInstance = getWeakInstance(value, components);
		if (weakInstance != null)
			rollbackAndThrowException(new ExistsException("Attempts to add an already existing instance : " + weakInstance.info()));
		T instance = buildInstance(overrides, value, Arrays.asList(components));
		return (T) ((AbstractVertex<?>) instance).rebuildAll(() -> ((AbstractVertex<?>) instance).plug());
	}

	@Override
	@SuppressWarnings("unchecked")
	public T setInstance(List<T> overrides, Serializable value, T... components) {
		checkSameEngine(Arrays.asList(components));
		checkSameEngine(overrides);
		T nearestMeta = adjustMeta(overrides, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.setInstance(overrides, value, components);
		T weakInstance = getWeakInstance(value, components);
		if (weakInstance != null) {
			if (weakInstance.equiv(this, value, Arrays.asList(components)))
				return weakInstance;
			return weakInstance.update(overrides, value, components);
		}
		T instance = buildInstance(overrides, value, Arrays.asList(components));
		return (T) ((AbstractVertex<?>) instance).rebuildAll(() -> ((AbstractVertex<?>) instance).plug());
	}

}
