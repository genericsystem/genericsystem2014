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
		overrides.forEach(Signature::checkIsAlive);
		init(meta, () -> computeSupers(overrides), value, components);
		checkOverrides(overrides);
		return (T) this;
	}

	public T initFromSupers(T meta, List<T> supers, Serializable value, List<T> components) {
		return init(meta, () -> supers, value, components);
	}

	@SuppressWarnings("unchecked")
	private T init(T meta, Supplier<List<T>> supersSupplier, Serializable value, List<T> components) {
		super.init(meta, value, components);
		this.supers = supersSupplier.get();
		this.supers.forEach(Signature::checkIsAlive);

		checkSupers();
		checkDependsSuperComponents();
		return (T) this;
	}

	private void checkOverrides(List<T> overrides) {
		if (!overrides.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))))
			rollbackAndThrowException(new IllegalStateException("Inconsistant overrides : " + overrides + " " + supers));
	}

	private void checkSupers() {
		if (!getSupersStream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!getSupersStream().noneMatch(superVertex -> superVertex.equals(this)))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
	}

	private void checkDependsSuperComponents() {
		getSupersStream().forEach(superVertex -> {
			if (!(componentsDepends(getComponents(), superVertex.getComponents())))
				rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}

	@Override
	public Stream<T> getSupersStream() {
		return supers.stream();
	}

}
