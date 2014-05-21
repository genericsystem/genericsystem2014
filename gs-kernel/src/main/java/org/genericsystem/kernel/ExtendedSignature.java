package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ExtendedSignature<T extends ExtendedSignature<T>> extends Signature<T> {

	private List<T> supers;

	@SuppressWarnings("unchecked")
	public T initFromOverrides(T meta, List<T> overrides, Serializable value, List<T> components) {
		init(meta, () -> computeSupers(overrides), value, components);
		checkSupersOrOverrides(overrides);
		checkSupersOrOverrides(supers);
		checkOverridesAreReached(overrides);
		checkDependsSuperComponents(overrides);
		return (T) this;
	}

	public T initFromSupers(T meta, List<T> supers, Serializable value, List<T> components) {
		return init(meta, () -> supers, value, components);
	}

	@SuppressWarnings("unchecked")
	private T init(T meta, Supplier<List<T>> supersSupplier, Serializable value, List<T> components) {
		super.init(meta, value, components);
		this.supers = supersSupplier.get();
		checkSupersOrOverrides(this.supers);
		return (T) this;
	}

	private void checkSupersOrOverrides(List<T> overrides) {
		overrides.forEach(Signature::checkIsAlive);
		if (!overrides.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!overrides.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!overrides.stream().noneMatch(this::equals))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
	}

	private void checkOverridesAreReached(List<T> overrides) {
		if (!overrides.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))))
			rollbackAndThrowException(new IllegalStateException("Inconsistant overrides : " + overrides + " " + supers));
	}

	private void checkDependsSuperComponents(List<T> overrides) {
		getSupersStream().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), overrides, getValue(), getComponents()))
				rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});

		// getSupersStream().forEach(superVertex -> {
		// if (!(componentsDepends(getComponents(), superVertex.getComponents())))
		// rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		// });
	}

	@Override
	public Stream<T> getSupersStream() {
		return supers.stream();
	}

}
