package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.CrossEnginesAssignementsException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T> {

	private T meta;
	private List<T> components;
	private Serializable value;

	protected List<T> supers;

	@SuppressWarnings("unchecked")
	protected T init(T meta, List<T> supers, Serializable value, List<T> components) {
		init(meta, value, components);
		this.supers = Collections.unmodifiableList(supers);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	protected T init(T meta, Serializable value, List<T> components) {
		this.meta = meta != null ? meta : (T) this;
		this.value = value;
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
	public List<T> getSupers() {
		return supers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultRoot<T> getRoot() {
		return this != meta ? meta.getRoot() : getSupers().isEmpty() ? (DefaultRoot<T>) this : getSupers().get(0).getRoot();
	}

	@Override
	public Context<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	protected abstract Dependencies<T> getInstancesDependencies();

	protected abstract Dependencies<T> getInheritingsDependencies();

	protected abstract Dependencies<T> getCompositesDependencies();

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
		T adjustMeta = getMeta().adjustOrBuildMeta(newValue, newComponentsList);
		return getCurrentCache().getBuilder().rebuildAll((T) this, () -> {
			T equivInstance = adjustMeta.getDirectInstance(newValue, newComponentsList);
			return equivInstance != null ? equivInstance : getCurrentCache().getBuilder().build(getClass(), adjustMeta, overrides, newValue, newComponentsList);
		}, computeDependencies());
	}

	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		T adjustedMeta = adjustOrBuildMeta(value, componentList);
		if (adjustedMeta.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, adjustedMeta.getSupers()))
			getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + adjustedMeta.info()));

		T equivInstance = adjustedMeta.getDirectInstance(value, componentList);
		if (equivInstance != null)
			getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + equivInstance.info()));
		return getCurrentCache().getBuilder().rebuildAll(null, () -> getCurrentCache().getBuilder().build(clazz, adjustedMeta, overrides, value, componentList), adjustedMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	protected T setInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		T adjustedMeta = adjustOrBuildMeta(value, componentList);
		if (adjustedMeta.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, adjustedMeta.getSupers()))
			return adjustedMeta;

		T equivInstance = adjustedMeta.getDirectEquivInstance(value, componentList);
		if (equivInstance != null)
			return equivInstance.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : equivInstance.update(overrides, value, components);
		return getCurrentCache().getBuilder().rebuildAll(null, () -> getCurrentCache().getBuilder().build(clazz, adjustedMeta, overrides, value, componentList), adjustedMeta.computePotentialDependencies(overrides, value, componentList));
	}

	@SuppressWarnings("unchecked")
	T getMeta(int dim) {
		T adjustedMeta = ((T) getRoot()).adjustMeta(dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@SuppressWarnings("unchecked")
	public T setMeta(int dim) {
		T adjustedMeta = ((T) getRoot()).adjustMeta(dim);
		return adjustedMeta.getComponents().size() == dim ? adjustedMeta : getCurrentCache().getBuilder().buildMeta(adjustedMeta, dim);
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
	T adjustOrBuildMeta(Serializable value, List<T> components) {
		if (isMeta()) {
			T adjustedMeta = ((T) getRoot()).adjustMeta(components.size());
			return adjustedMeta.getComponents().size() == components.size() ? adjustedMeta : getCurrentCache().getBuilder().buildMeta(adjustedMeta, components.size());
		}
		return adjustMeta(value, components);
	}

	protected T adjustMeta(Serializable value, @SuppressWarnings("unchecked") T... components) {
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
	protected T adjustMeta(int dim) {
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

	@SuppressWarnings("unchecked")
	Snapshot<T> getInheritings(final T origin, final int level) {
		return () -> new InheritanceComputer<>((T) AbstractVertex.this, origin, level).inheritanceStream();
	}

	abstract protected T newT();

	abstract protected T[] newTArray(int dim);

	@SuppressWarnings("unchecked")
	@Override
	public T[] coerceToTArray(Object... array) {
		T[] result = getCurrentCache().getBuilder().newTArray(array.length);
		for (int i = 0; i < array.length; i++)
			result[i] = (T) array[i];
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] addThisToTargets(T... targets) {
		T[] composites = getCurrentCache().getBuilder().newTArray(targets.length + 1);
		composites[0] = (T) this;
		System.arraycopy(targets, 0, composites, 1, targets.length);
		return composites;
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
					getCurrentCache().getChecker().checkIsAlive((T) this);
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
	protected T plug() {
		return getCurrentCache().plug((T) this);
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

}
