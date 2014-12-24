package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T> {
	private T meta;
	private List<T> components;
	private Serializable value;
	private List<T> supers;

	@SuppressWarnings("unchecked")
	protected T init(T meta, List<T> supers, Serializable value, List<T> components) {
		this.meta = meta != null ? meta : (T) this;
		this.value = value;
		this.components = Collections.unmodifiableList(new ArrayList<>(components));
		this.supers = Collections.unmodifiableList(new ArrayList<>(supers));
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

	@SuppressWarnings("unchecked")
	protected void forceRemove() {
		getCurrentCache().forceRemove((T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void remove() {
		getCurrentCache().remove((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T update(List<T> overrides, Serializable newValue, T... newComponents) {
		return getCurrentCache().getBuilder().update((T) this, overrides, newValue, Arrays.asList(newComponents));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T addInstance(List<T> overrides, Serializable value, T... components) {
		return addInstance(null, overrides, value, Arrays.asList(components));
	}
	@Override
	@SuppressWarnings("unchecked")
	public T setInstance(List<T> overrides, Serializable value, T... components) {
		return setInstance(null, overrides, value, Arrays.asList(components));
	}
	@SuppressWarnings("unchecked")
	T addInstance(Class<?> clazz, List<T> overrides, Serializable value, List<T> components) {
		getCurrentCache().getChecker().checkBeforeBuild(clazz, (T) this, overrides, value, components);
		T adjustedMeta = writeAdjustMeta(value, components);
		T equalsInstance = adjustedMeta.getDirectInstance(value, components);
		if (equalsInstance != null)
			getCurrentCache().discardWithException(new ExistsException("An equivalent instance already exists : " + equalsInstance.info()));
		return getCurrentCache().getBuilder().internalSetInstance(null, clazz, adjustedMeta, overrides, value, components);
	}

	@SuppressWarnings("unchecked")
	T setInstance(Class<?> clazz, List<T> overrides, Serializable value, List<T> components) {
		getCurrentCache().getChecker().checkBeforeBuild(clazz, (T) this, overrides, value, components);
		T adjustedMeta = writeAdjustMeta(value, components);
		T equivInstance = adjustedMeta.getDirectEquivInstance(value, components);
		if (equivInstance != null && equivInstance.equalsAndOverrides(adjustedMeta, overrides, value, components))
			return equivInstance;
		return getCurrentCache().getBuilder().internalSetInstance(equivInstance, clazz, adjustedMeta, overrides, value, components);
	}

	@SuppressWarnings("unchecked")
	T writeAdjustMeta(Serializable value, List<T> components) {
		T meta = (T)this;
		if( isMeta())
			meta = ((T) getRoot()).setMeta(components.size());
		return meta.readAdjustMeta(value, components);
	}

	T setMeta(int dim) {
		return setMeta(null, dim);
	}

	T setMeta(Class<?> clazz, int dim) {
		return getCurrentCache().getBuilder().setMeta(clazz, dim);
	}

	@SuppressWarnings("unchecked")
	T readAdjustMeta(Serializable value, List<T> components) {
		T result = null;
		if (!components.equals(getComponents()))
			for (T directInheriting : getInheritings()) {
				if (componentsDepends(components, directInheriting.getComponents())) {
					if (result == null)
						result = directInheriting;
					else
						getCurrentCache().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? (T) this : result.readAdjustMeta(value, components);
	}

	protected T getDirectInstance(Serializable value, List<T> components) {
		for (T instance : getInstances())
			if (((AbstractVertex<?>) instance).equalsRegardlessSupers(this, value, components))
				return instance;
		return null;
	}

	T getDirectInstance(List<T> overrides, Serializable value, List<T> components) {
		if (isMeta() && equalsRegardlessSupers(this, value, components))
			return (T) this;
		T result = getDirectInstance(value, components);
		return result != null && Statics.areOverridesReached(result.getSupers(), overrides) ? result : null;
	}

	boolean isDependencyOf(T meta, List<T> supers, Serializable value, List<T> components) {
		return inheritsFrom(meta, value, components) || getComponents().stream().filter(component -> component != null).anyMatch(component -> component.isDependencyOf(meta, supers, value, components))
				|| (!isMeta() && getMeta().isDependencyOf(meta, supers, value, components)) || (!components.isEmpty() && componentsDepends(getComponents(), components) && supers.stream().anyMatch(override -> override.inheritsFrom(getMeta())));
	}

	@SuppressWarnings("unchecked")
	T getDirectEquivInstance(Serializable value, List<T> components) {
		if (isMeta() && equalsRegardlessSupers(this, value, components))
			return (T) this;
		for (T instance : getInstances())
			if (instance.equiv(this, value, components))
				return instance;
		return null;
	}

	boolean equalsAndOverrides(T meta, List<T> overrides, Serializable value, List<T> components) {
		return equalsRegardlessSupers(meta, value, components) && Statics.areOverridesReached(getSupers(), overrides);
	}

	boolean equals(ISignature<?> meta, List<? extends ISignature<?>> supers, Serializable value, List<? extends ISignature<?>> components) {
		return equalsRegardlessSupers(meta, value, components) && getSupers().equals(supers);
	}

	boolean equalsRegardlessSupers(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (!getMeta().equals(meta == null ? this : meta))
			return false;
		if (!Objects.equals(getValue(), value))
			return false;
		List<T> componentsList = getComponents();
		if (componentsList.size() != components.size())
			return false;
		return componentsList.equals(components);
	}

	public boolean genericEquals(ISignature<?> service) {
		if (service == null)
			return false;
		if (this == service)
			return true;
		if (!getMeta().genericEquals(service == service.getMeta() ? this : service.getMeta()))
			return false;
		if (!Objects.equals(getValue(), service.getValue()))
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

	private static <T extends AbstractVertex<T>> boolean componentEquiv(T component, ISignature<?> compare) {
		return (component == compare) || (component != null && component.equiv(compare));
	}

	boolean equiv(ISignature<? extends ISignature<?>> service) {
		if (service == null)
			return false;
		if (this == service)
			return true;
		return equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	boolean equiv(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (!getMeta().equals(meta == null ? this : meta))
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

	@SuppressWarnings("unchecked")
	Snapshot<T> getInheritings(final T origin, final int level) {
		return () -> new InheritanceComputer<>((T) AbstractVertex.this, origin, level).inheritanceStream();
	}

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

	@SuppressWarnings("unchecked")
	boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if ((subComponent == null && superComponent == null) || (subComponent != null && superComponent != null && subComponent.isSpecializationOf(superComponent))
						|| (subComponent == null && superComponent != null && this.isSpecializationOf(superComponent)) || (subComponent != null && superComponent == null && subComponent.isSpecializationOf((T) this))) {
					if (isSingularConstraintEnabled(subIndex))
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

	T getMap() {
		return getRoot().find(SystemMap.class);
	}

	Optional<T> getKey(AxedPropertyClass property) {
		T map = getMap();
		Stream<T> keys = map != null ? getAttributes(map).get() : Stream.empty();
		return keys.filter(x -> Objects.equals(x.getValue(), property)).findFirst();
	}
}
