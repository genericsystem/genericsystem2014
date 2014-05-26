package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ExtendedSignature<T extends ExtendedSignature<T>> extends Signature<T> {

	protected List<T> supers;

	@SuppressWarnings("unchecked")
	public T init(int level, T meta, List<T> overrides, Serializable value, List<T> components) {
		super.init(level, meta, value, components);
		this.supers = overrides;
		checkSupersOrOverrides(overrides);
		checkOverridesAreReached(overrides);
		checkDependsSuperComponents(overrides);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T computeSupers() {
		this.supers = new ArrayList<T>(new SupersComputer<>(level, meta, this.supers, value, components));
		checkSupersOrOverrides(this.supers);
		return (T) this;
	}

	protected void checkSupersOrOverrides(List<T> overrides) {
		overrides.forEach(Signature::checkIsAlive);
		if (!overrides.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!overrides.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!overrides.stream().noneMatch(this::equals))
			rollbackAndThrowException(new IllegalStateException("Supers loop detected : " + info()));
	}

	private void checkOverridesAreReached(List<T> overrides) {
		if (!overrides.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))))
			rollbackAndThrowException(new IllegalStateException("Unable to reach overrides : " + overrides + " for : " + info()));
	}

	private void checkDependsSuperComponents(List<T> overrides) {
		getSupersStream().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), overrides, getValue(), getComponents()))
				rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}

	@Override
	public Stream<T> getSupersStream() {
		return supers.stream();
	}

}
