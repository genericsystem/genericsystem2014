package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.CrossEnginesAssignementsException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.GetInstanceConstraintViolationException;
import org.genericsystem.api.exception.MetaLevelConstraintViolationException;
import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.annotations.Priority;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T> {

	private T meta;
	private List<T> components;
	private Serializable value;

	@SuppressWarnings("unchecked")
	@Override
	public DefaultRoot<T> getRoot() {
		return this != meta ? meta.getRoot() : getSupers().isEmpty() ? (DefaultRoot<T>) this : getSupers().get(0).getRoot();
	}

	@SuppressWarnings("unchecked")
	protected T init(T meta, Serializable value, List<T> components) {
		if (meta != null) {
			meta.checkIsAlive();
			this.meta = meta;
		} else
			this.meta = (T) this;
		this.value = value;
		components.stream().filter(x -> x != null).forEach(x -> x.checkIsAlive());
		this.components = Collections.unmodifiableList(components);
		return (T) this;
	}

	@Override
	public T getMeta() {
		return meta;
	}

	@Override
	public List<T> getComponents() {
		return components;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public DefaultContext<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	protected List<T> supers;

	protected abstract Dependencies<T> getInstancesDependencies();

	protected abstract Dependencies<T> getInheritingsDependencies();

	protected abstract Dependencies<T> getCompositesDependencies();

	@SuppressWarnings("unchecked")
	protected T init(T meta, List<T> supers, Serializable value, List<T> components) {
		init(meta, value, components);
		this.supers = Collections.unmodifiableList(supers);
		return (T) this;
	}

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT(clazz).init(meta, supers, value, components);
	}

	protected T newT(Class<?> clazz) {
		return newT();
	}

	@Override
	public List<T> getSupers() {
		return supers;
	}

	protected Dependencies<T> buildDependencies() {
		return new DependenciesImpl<>();
	}

	protected void forceRemove() {
		computeDependencies().forEach(T::unplug);
	}

	private Iterable<T> getOrderedDependenciesToRemove() {
		return Statics.reverseCollections(buildOrderedDependenciesToRemove());
	}

	@Override
	public void remove() {
		getOrderedDependenciesToRemove().forEach(T::unplug);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T update(List<T> overrides, Serializable newValue, T... newComponents) {
		List<T> newComponentsList = Arrays.asList(newComponents);
		T adjustMeta = getMeta().ajustOrBuildMeta(newValue, newComponentsList);
		return rebuildAll((T) this, () -> {
			T equivInstance = adjustMeta.getDirectInstance(newValue, newComponentsList);
			return equivInstance != null ? equivInstance : build(getClass(), adjustMeta, overrides, newValue, newComponentsList);
		}, computeDependencies());
	}

	private class ConvertMap extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		T convert(T dependency) {
			if (dependency.isAlive())
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				if (dependency.isMeta())
					newDependency = setMeta(dependency.getComponents().size());
				else {
					List<T> overrides = dependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
					List<T> components = dependency.getComponents().stream().map(x -> x != null ? convert(x) : null).collect(Collectors.toList());
					T adjustMeta = convert(dependency.getMeta()).adjustMeta(dependency.getValue(), components);
					T equivInstance = adjustMeta.getDirectInstance(dependency.getValue(), components);
					newDependency = equivInstance != null ? equivInstance : build(dependency.getClass(), adjustMeta, overrides, dependency.getValue(), components);
				}
				put(dependency, newDependency);
			}
			return newDependency;
		}
	}

	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		T adjustedMeta = ajustOrBuildMeta(value, componentList);
		if (adjustedMeta.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, adjustedMeta.getSupers()))
			getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + adjustedMeta.info()));

		T equivInstance = adjustedMeta.getDirectInstance(value, componentList);
		if (equivInstance != null)
			getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + equivInstance.info()));
		return rebuildAll(null, () -> adjustedMeta.build(clazz, adjustedMeta, overrides, value, componentList), adjustedMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	protected T setInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		T adjustedMeta = ajustOrBuildMeta(value, componentList);
		if (adjustedMeta.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, adjustedMeta.getSupers()))
			return adjustedMeta;

		T equivInstance = adjustedMeta.getDirectEquivInstance(value, componentList);
		if (equivInstance != null)
			return equivInstance.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : equivInstance.update(overrides, value, components);
		return rebuildAll(null, () -> adjustedMeta.build(clazz, adjustedMeta, overrides, value, componentList), adjustedMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	T getMeta(int dim) {
		T adjustedMeta = ((T) getRoot()).adjustMeta(dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@SuppressWarnings("unchecked")
	public T setMeta(int dim) {
		T adjustedMeta = ((T) getRoot()).adjustMeta(dim);
		return adjustedMeta.getComponents().size() == dim ? adjustedMeta : buildMeta(adjustedMeta, dim);
	}

	// protected T setInheriting(Class<?> clazz, T meta, List<T> overrides, Serializable value, T... components) {
	// overrides.add((T) this);
	// List<T> componentList = Arrays.asList(components);
	// T adjustedMeta = adjustMeta(components.length);
	// if (adjustedMeta.getComponents().size() == components.length)
	// return adjustedMeta;
	//
	// T equivInheriting = getDirectEquivInheriting(meta, value, componentList);
	// if (equivInheriting != null)
	// return equivInheriting.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInheriting.getSupers()) ? equivInheriting : equivInheriting.update(overrides, value, components);
	// return rebuildAll(null, () -> build(clazz, meta, overrides, value, componentList), computePotentialDependencies(meta, overrides, value, componentList));
	// }

	@SuppressWarnings("unchecked")
	T build(Class<?> clazz, T adjustMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = new ArrayList<>(new SupersComputer<>((T) getRoot(), adjustMeta, overrides, value, components));// TODO Order supers
		checkOverridesAreReached(overrides, supers);// TODO system constraints
		return newT(clazz, adjustMeta, supers, value, components).plug();
	}

	@SuppressWarnings("unchecked")
	T getAlive() {
		if (isRoot())
			return (T) this;
		if (isMeta()) {
			T aliveMeta = getSupers().get(0).getAlive();
			if (aliveMeta != null)
				for (T inheritings : aliveMeta.getInheritings())
					if (equals(inheritings))
						return inheritings;
		} else {
			T aliveMeta = getMeta().getAlive();
			if (aliveMeta != null)
				for (T instance : aliveMeta.getInstances())
					if (equals(instance))
						return instance;
		}
		return null;
	}

	protected LinkedHashSet<T> computeDependencies() {
		return new DependenciesComputer<T>() {
			private static final long serialVersionUID = 4116681784718071815L;

			@Override
			boolean checkDependency(T node) {
				return isAncestorOf(node);
			}
		}.visit(getMeta());
	}

	@SuppressWarnings("unchecked")
	private LinkedHashSet<T> buildOrderedDependenciesToRemove() {
		return new LinkedHashSet<T>() {
			private static final long serialVersionUID = -3610035019789480505L;
			{
				visit((T) AbstractVertex.this);
			}

			public void visit(T generic) {
				if (add(generic)) {// protect from loop
					if (!generic.getInheritings().isEmpty() || !generic.getInstances().isEmpty())
						getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException("Ancestor : " + generic + " has an inheritance or instance dependency"));

					for (T composite : generic.getComposites())
						if (!generic.equals(composite)) {
							for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
								if (/* !componentDependency.isAutomatic() && */composite.getComponents().get(componentPos).equals(generic) && !contains(composite) && composite.isReferentialIntegrityEnabled(componentPos))
									getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + generic + " by composite position : " + componentPos));
							visit(composite);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (generic.isCascadeRemove(axe))
							visit(generic.getComponents().get(axe));
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	protected LinkedHashSet<T> computePotentialDependencies(List<T> overrides, Serializable value, List<T> components) {
		return new DependenciesComputer<T>() {
			private static final long serialVersionUID = -3611136800445783634L;

			@Override
			boolean checkDependency(T node) {
				return node.dependsFrom((T) AbstractVertex.this, overrides, value, components);
			}
		}.visit((T) this);
	}

	@SuppressWarnings("unchecked")
	T ajustOrBuildMeta(Serializable value, List<T> components) {
		if (isMeta()) {
			T adjustedMeta = ((T) getRoot()).adjustMeta(components.size());
			return adjustedMeta.getComponents().size() == components.size() ? adjustedMeta : buildMeta(adjustedMeta, components.size());
		}
		return adjustMeta(value, components);
	}

	@SuppressWarnings("unchecked")
	T buildMeta(T adjustedMeta, int dim) {
		T root = (T) getRoot();
		List<T> components = new ArrayList<>();
		for (int i = 0; i < dim; i++)
			components.add(root);
		List<T> supers = Collections.singletonList(adjustedMeta);
		return root.rebuildAll(null, () -> root.newT(null, null, supers, root.getValue(), components).plug(), adjustedMeta.computePotentialDependencies(supers, root.getValue(), components));

	}

	public T adjustMeta(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return adjustMeta(value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	T adjustMeta(Serializable value, List<T> components) {
		T result = null;
		if (!components.equals(getComponents()))
			for (T directInheriting : getInheritings()) {
				if (componentsDepends(components, directInheriting.getComponents())) {
					if (result == null)
						result = directInheriting;
					else
						getRoot().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? (T) this : result.adjustMeta(value, components);
	}

	@SuppressWarnings("unchecked")
	T adjustMeta(int dim) {
		assert isMeta();
		int size = getComponents().size();
		if (size > dim)
			return null;
		if (size == dim)
			return (T) this;
		T directInheriting = getInheritings().first();
		return directInheriting != null && directInheriting.getComponents().size() <= dim ? directInheriting.adjustMeta(dim) : (T) this;
	}

	protected T getDirectInstance(Serializable value, List<T> components) {
		for (T instance : getInstances())
			if (((AbstractVertex<?>) instance).equalsRegardlessSupers(this, value, components))
				return instance;
		return null;
	}

	T getDirectInstance(List<T> overrides, Serializable value, List<T> components) {
		T result = getDirectInstance(value, components);
		return result != null && Statics.areOverridesReached(overrides, result.getSupers()) ? result : null;
	}

	boolean dependsFrom(T meta, List<T> overrides, Serializable value, List<T> components) {
		return inheritsFrom(meta, value, components) || getComponents().stream().filter(component -> component != null).anyMatch(component -> component.dependsFrom(meta, overrides, value, components))
				|| (!isMeta() && getMeta().dependsFrom(meta, overrides, value, components)) || (!components.isEmpty() && componentsDepends(getComponents(), components) && overrides.stream().anyMatch(override -> override.inheritsFrom(getMeta())));
	}

	T getDirectEquivInstance(Serializable value, List<T> components) {
		for (T instance : getInstances())
			if (instance.equiv(this, value, components))
				return instance;
		return null;
	}

	boolean equals(ISignature<?> meta, List<? extends ISignature<?>> supers, Serializable value, List<? extends ISignature<?>> components) {
		return equalsRegardlessSupers(meta, value, components) && getSupers().equals(supers);
	}

	boolean equalsRegardlessSupers(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (!Objects.equals(getValue(), value))
			return false;
		if (this == getMeta()) {
			if (meta != null)
				if (meta != meta.getMeta())
					return false;
		} else if (!getMeta().equals(meta))
			return false;

		List<T> componentsList = getComponents();
		if (componentsList.size() != components.size())
			return false;
		return componentsList.equals(components);
	}

	public boolean genericEquals(ISignature<?> service) {
		if (this == service)
			return true;
		if (!Objects.equals(getValue(), service.getValue()))
			return false;
		if (this.genericEquals(getMeta())) {
			if (service != service.getMeta())
				return false;
		} else if (!getMeta().genericEquals(service.getMeta()))
			return false;

		List<T> componentsList = getComponents();
		if (componentsList.size() != service.getComponents().size())
			return false;

		for (int i = 0; i < componentsList.size(); i++)
			if (!componentsGenericEquals(componentsList.get(i), service.getComponents().get(i)))
				return false;

		List<T> supersList = getSupers();
		if (supersList.size() != service.getSupers().size())
			return false;
		for (int i = 0; i < supersList.size(); i++)
			if (!supersList.get(i).genericEquals(service.getSupers().get(i)))
				return false;
		return true;
	}

	static <T extends AbstractVertex<T>> boolean componentsGenericEquals(AbstractVertex<T> component, ISignature<?> compare) {
		return (component == compare) || (component != null && component.genericEquals(compare));
	}

	static <T extends AbstractVertex<T>> boolean componentEquiv(T component, ISignature<?> compare) {
		return (component == compare) || (component != null && component.equiv(compare));
	}

	boolean equiv(ISignature<? extends ISignature<?>> service) {
		if (service == null)
			return false;
		if (this == service)
			return true;
		if (this == getMeta()) {
			if (service.getMeta() != service.getMeta().getMeta())
				return false;
		} else if (!getMeta().equiv(service.getMeta()))
			return false;

		if (getComponents().size() != service.getComponents().size())
			return false;
		List<T> componentsList = getComponents();
		List<? extends ISignature<?>> serviceComponents = service.getComponents();
		for (int i = 0; i < componentsList.size(); i++)
			if (!isReferentialIntegrityEnabled(i) && isSingularConstraintEnabled(i))
				return componentEquiv(componentsList.get(i), serviceComponents.get(i));
		for (int i = 0; i < componentsList.size(); i++)
			if (!componentEquiv(componentsList.get(i), serviceComponents.get(i)))
				return false;
		if (!getMeta().isPropertyConstraintEnabled())
			return Objects.equals(getValue(), service.getValue());
		return true;
	}

	boolean equiv(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (this == getMeta()) {
			if (meta != null && meta != meta.getMeta())
				return false;
		} else if (!getMeta().equiv(meta))
			return false;

		List<T> componentsList = getComponents();
		if (componentsList.size() != components.size())
			return false;
		for (int i = 0; i < componentsList.size(); i++)
			if (!isReferentialIntegrityEnabled(i) && isSingularConstraintEnabled(i))
				return componentEquiv(componentsList.get(i), components.get(i));
		for (int i = 0; i < componentsList.size(); i++)
			if (!componentEquiv(componentsList.get(i), components.get(i)))
				return false;
		if (!getMeta().isPropertyConstraintEnabled())
			return Objects.equals(getValue(), value);
		return true;
	}

	// TODO move this check in system constraints!
	private void checkSameEngine(List<T> generics) {
		if (generics.stream().anyMatch(generic -> generic != null && !generic.getRoot().equals(getRoot())))
			getRoot().discardWithException(new CrossEnginesAssignementsException());
	}

	T rebuildAll(T toRebuild, Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.forEach(T::unplug);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(toRebuild);
		ConvertMap convertMap = new ConvertMap();
		convertMap.put(toRebuild, build);
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));
		return build;
	}

	@SuppressWarnings("unchecked")
	Snapshot<T> getInheritings(final T origin, final int level) {
		return () -> new InheritanceComputer<>((T) AbstractVertex.this, origin, level).inheritanceStream();
	}

	abstract protected T newT();

	abstract protected T[] newTArray(int dim);

	@SuppressWarnings("unchecked")
	@Override
	public T[] coerceToTArray(Object... array) {
		T[] result = newTArray(array.length);
		for (int i = 0; i < array.length; i++)
			result[i] = (T) array[i];
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] addThisToTargets(T... targets) {
		T[] composites = newTArray(targets.length + 1);
		composites[0] = (T) this;
		System.arraycopy(targets, 0, composites, 1, targets.length);
		return composites;
	}

	void checkOverridesAreReached(List<T> overrides, List<T> supers) {
		if (!Statics.areOverridesReached(overrides, supers))
			getRoot().discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
	}

	static interface SingularsLazyCache {
		boolean get(int i);
	}

	boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		class SingularsLazyCacheImpl implements SingularsLazyCache {
			private final Boolean[] singulars = new Boolean[subComponents.size()];

			@Override
			public boolean get(int i) {
				return singulars[i] != null ? singulars[i] : (singulars[i] = isSingularConstraintEnabled(i));
			}
		}
		return componentsDepends(new SingularsLazyCacheImpl(), subComponents, superComponents);
	}

	// boolean componentsDepends(SingularsLazyCache singulars, List<T> subComponents, List<T> superComponents) {
	// int subIndex = 0;
	// loop: for (T superComponent : superComponents) {
	// for (; subIndex < subComponents.size(); subIndex++) {
	// T subComponent = subComponents.get(subIndex);
	// if (subComponent == null && superComponent == null)
	// continue loop;
	// else if (subComponent == null || superComponent == null)
	// return false;
	// else if (subComponent.isSpecializationOf(superComponent)) {
	// if (singulars.get(subIndex))
	// return true;
	// subIndex++;
	// continue loop;
	// }
	// }
	// return false;
	// }
	// return true;
	// }

	@SuppressWarnings("unchecked")
	private boolean componentsDepends(SingularsLazyCache singulars, List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if ((subComponent == null && superComponent == null) || (subComponent != null && superComponent != null && subComponent.isSpecializationOf(superComponent))
						|| (subComponent == null && superComponent != null && this.isSpecializationOf(superComponent)) || (subComponent != null && superComponent == null && subComponent.isSpecializationOf((T) this))) {
					if (singulars.get(subIndex))
						return true;
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || isSuperOf(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	protected boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents) {
		return isSuperOf(getMeta(), getValue(), getComponents(), superMeta, superValue, superComponents);
	}

	private static <T extends AbstractVertex<T>> boolean isSuperOf(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (subMeta == null) {
			if (!superMeta.isMeta())
				return false;
		} else if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!superMeta.componentsDepends(subComponents, superComponents))
			return false;
		if (superMeta.isPropertyConstraintEnabled())
			return !subComponents.equals(superComponents);
		return Objects.equals(subValue, superValue);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	protected <subT extends T> subT plug() {
		return (subT) getCurrentCache().plug((T) this);
	}

	@SuppressWarnings("unchecked")
	protected boolean unplug() {
		return getCurrentCache().unplug((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	T getMap() {
		return getRoot().getMetaAttribute().getDirectInstance(SystemMap.class, Collections.singletonList((T) getRoot()));
	}

	public static class SystemMap {
	}

	private Stream<T> getKeys() {
		T map = getMap();
		return map != null ? getAttributes(map).get() : Stream.empty();
	}

	Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> Objects.equals(x.getValue(), property)).findFirst();
	}

	private Stream<T> getKeys(Class<?> propertyClass) {
		return getKeys().filter(x -> x.getValue() instanceof AxedPropertyClass && Objects.equals(((AxedPropertyClass) x.getValue()).getClazz(), propertyClass));
	}

	protected void checkSystemConstraints(boolean isOnAdd, boolean isFlushTime) {
		if (isMeta())
			checkMeta();
		if (!isFlushTime)
			checkIsAlive();
		else if (!isOnAdd && isAlive())
			getRoot().discardWithException(new AliveConstraintViolationException(info()));
		if (!isOnAdd)
			checkDependenciesAreEmpty();
		checkDependsMetaComponents();
		checkSupers();
		checkDependsSuperComponents();
		checkLevel();
		checkLevelComponents();
		checkGetInstance();
	}

	private void checkMeta() {
		if (!getComponents().stream().allMatch(c -> c.isRoot()) || !Objects.equals(getValue(), getRoot().getValue()) || getSupers().size() != 1 || !getSupers().get(0).isMeta())
			getRoot().discardWithException(new IllegalStateException("Malformed meta : " + info()));
	}

	private void checkDependenciesAreEmpty() {
		if (!getInstances().isEmpty() || !getInheritings().isEmpty() || !getComposites().isEmpty())
			getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException("Unable to remove : " + info() + " cause it has dependencies"));
	}

	@SuppressWarnings("unchecked")
	private void checkDependsMetaComponents() {
		if (getMeta().getComponents().size() != getComponents().size())
			getRoot().discardWithException(new MetaRuleConstraintViolationException("Added generic and its meta do not have the same components size. Added node components : " + getComponents() + " and meta components : " + getMeta().getComponents()));
		for (int pos = 0; pos < getComponents().size(); pos++) {
			T component = getComponent(pos);
			T metaComponent = getMeta().getComponent(pos);
			if (component == null)
				if (metaComponent == null)
					continue;
				else
					component = (T) this;
			else if (metaComponent == null)
				metaComponent = getMeta();
			if (!component.isInstanceOf(metaComponent) && !component.inheritsFrom(metaComponent))
				getRoot().discardWithException(new MetaRuleConstraintViolationException("Component of added generic : " + component + " must be instance of or must inherits from the component of its meta : " + metaComponent));
		}
	}

	private void checkLevelComponents() {
		for (T component : getComponents())
			if ((component == null ? getLevel() : component.getLevel()) > getLevel())
				getRoot().discardWithException(new MetaLevelConstraintViolationException("Inappropriate component meta level : " + component.getLevel() + " for component : " + component + ". Component meta level for added node is : " + getLevel()));
	}

	private void checkLevel() {
		if (getLevel() > Statics.CONCRETE)
			getRoot().discardWithException(new MetaLevelConstraintViolationException("Unable to instanciate a concrete generic : " + getMeta()));
	}

	private void checkSupers() {
		supers.forEach(AbstractVertex::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers (bad level) : " + supers));
		if (!supers.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers : " + supers));
		if (!supers.stream().noneMatch(this::equals))
			getRoot().discardWithException(new IllegalStateException("Supers loop detected : " + info()));
		if (supers.stream().anyMatch(superVertex -> Objects.equals(superVertex.getValue(), getValue()) && superVertex.getComponents().equals(getComponents()) && getMeta().inheritsFrom(superVertex.getMeta())))
			getRoot().discardWithException(new IllegalStateException("Collision detected : " + info()));
	}

	private void checkDependsSuperComponents() {
		getSupers().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), supers, getValue(), getComponents()))
				getRoot().discardWithException(new IllegalStateException("Inconsistant components : " + getComponents()));
		});
	}

	private void checkGetInstance() {
		if (getMeta().getInstances().get().filter(x -> ((AbstractVertex<?>) x).equalsRegardlessSupers(getMeta(), getValue(), getComponents())).count() > 1)
			getRoot().discardWithException(new GetInstanceConstraintViolationException("get too many result for search : " + info()));
	}

	void checkConstraints(boolean isOnAdd, boolean isFlushTime) {
		if (getMap() != null) {
			Stream<T> contraintsHolders = getMeta().getHolders(getMap()).get().filter(holder -> holder.getMeta().getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) holder.getMeta().getValue()).getClazz()))
					.filter(holder -> holder.getValue() != null && !Boolean.FALSE.equals(holder.getValue())).sorted(CONSTRAINT_PRIORITY);
			contraintsHolders.forEach(constraintHolder -> {
				T baseComponent = constraintHolder.getBaseComponent();
				if (isSpecializationOf(baseComponent))
					check(constraintHolder, baseComponent, isFlushTime, isOnAdd, false);
				T targetComponent = constraintHolder.getTargetComponent();
				if (targetComponent != null && isSpecializationOf(targetComponent))
					check(constraintHolder, baseComponent, isFlushTime, isOnAdd, true);
			});
		}
	}

	@SuppressWarnings("unchecked")
	void check(T constraintHolder, T baseComponent, boolean isFlushTime, boolean isOnAdd, boolean isRevert) {
		try {
			constraintHolder.getMeta().statelessConstraint().check((T) this, baseComponent, constraintHolder.getValue(), ((AxedPropertyClass) constraintHolder.getMeta().getValue()).getAxe(), isOnAdd, isFlushTime, isRevert);
		} catch (ConstraintViolationException e) {
			getRoot().discardWithException(e);
		}
	}

	@SuppressWarnings("unchecked")
	Constraint<T> statelessConstraint() {
		try {
			return (Constraint<T>) ((AxedPropertyClass) getValue()).getClazz().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			getRoot().discardWithException(e);
		}
		return null;
	}

	int getConstraintPriority() {
		Class<?> clazz = ((AxedPropertyClass) getValue()).getClazz();
		Priority priority = clazz.getAnnotation(Priority.class);
		return priority != null ? priority.value() : 0;
	}

	void checkConsistency() {
		if (getMap() != null && getMeta().getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) getMeta().getValue()).getClazz()) && getValue() != null && !Boolean.FALSE.equals(getValue())) {
			T baseConstraint = getComponent(Statics.BASE_POSITION);
			int axe = ((AxedPropertyClass) getMeta().getValue()).getAxe();
			if (((AxedPropertyClass) getMeta().getValue()).getAxe() == Statics.NO_POSITION)
				baseConstraint.getAllInstances().forEach(x -> x.check((T) this, baseConstraint, true, true, false));
			else
				baseConstraint.getComponents().get(axe).getAllInstances().forEach(x -> x.check((T) this, baseConstraint, true, true, true));
		}
	}

	private static final Comparator<AbstractVertex<?>> CONSTRAINT_PRIORITY = new Comparator<AbstractVertex<?>>() {
		@Override
		public int compare(AbstractVertex<?> constraintHolder, AbstractVertex<?> compareConstraintHolder) {
			return constraintHolder.getMeta().getConstraintPriority() < compareConstraintHolder.getMeta().getConstraintPriority() ? -1 : 1;
		}
	};

}
