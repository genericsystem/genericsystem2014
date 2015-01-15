package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.kernel.Config.SystemMap;

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

	@Override
	public Context<T> getCurrentCache() {
		return (Context<T>) getRoot().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	protected T adjustMeta(Serializable value, T... components) {
		return adjustMeta(value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	T adjustMeta(Serializable value, List<T> components) {
		T result = null;
		if (!components.equals(getComponents()))
			for (T directInheriting : getInheritings()) {
				if (componentsDepends(components, directInheriting.getComponents())) {
					if (result == null) {
						result = directInheriting;
					} else
						getCurrentCache().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? (T) this : result.adjustMeta(value, components);
	}

	@SuppressWarnings("unchecked")
	protected T getDirectInstance(Serializable value, List<T> components) {
		if (isMeta() && equalsRegardlessSupers(this, value, components))
			return (T) this;
		for (T instance : getInstances())
			if (((AbstractVertex<?>) instance).equalsRegardlessSupers(this, value, components))
				return instance;
		return null;
	}

	T getDirectInstance(List<T> overrides, Serializable value, List<T> components) {
		T result = getDirectInstance(value, components);
		return result != null && Statics.areOverridesReached(result.getSupers(), overrides) ? result : null;
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

	@SuppressWarnings("unchecked")
	@Override
	public T getInstance(List<T> overrides, Serializable value, T... components) {
		List<T> componentsList = Arrays.asList(components);
		T adjustMeta = ((T) this).adjustMeta(value, componentsList);
		if (adjustMeta.getComponents().size() != components.length)
			return null;
		return adjustMeta.getDirectInstance(overrides, value, componentsList);
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
			if (!genericEquals(componentsList.get(i), service.getComponents().get(i)))
				return false;
		List<T> supersList = getSupers();
		if (supersList.size() != service.getSupers().size())
			return false;
		for (int i = 0; i < supersList.size(); i++)
			if (!supersList.get(i).genericEquals(service.getSupers().get(i)))
				return false;
		return true;
	}

	static <T extends AbstractVertex<T>> boolean genericEquals(AbstractVertex<T> component, ISignature<?> compare) {
		return (component == compare) || (component != null && component.genericEquals(compare));
	}

	private static <T extends AbstractVertex<T>> boolean equiv(T component, ISignature<?> compare) {
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
				return equiv(componentsList.get(i), components.get(i));
		for (int i = 0; i < componentsList.size(); i++)
			if (!equiv(componentsList.get(i), components.get(i)))
				return false;
		if (!getMeta().isPropertyConstraintEnabled())
			return Objects.equals(getValue(), value);
		return true;
	}

	@Override
	public T getMap() {
		return getRoot().find(SystemMap.class);
	}
}
