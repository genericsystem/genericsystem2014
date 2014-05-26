package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ExtendedSignature<T extends ExtendedSignature<T>> extends Signature<T> {

	protected List<T> supers;

	@SuppressWarnings("unchecked")
	public T init(int level, T meta, List<T> supers, Serializable value, List<T> components) {
		super.init(level, meta, value, components);
		this.supers = supers;
		checkSupers(supers);
		checkOverridesAreReached(supers);
		checkDependsSuperComponents(supers);
		return (T) this;
	}

	private void checkSupers(List<T> supers) {
		supers.forEach(Signature::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!supers.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!supers.stream().noneMatch(this::equals))
			rollbackAndThrowException(new IllegalStateException("Supers loop detected : " + info()));
	}

	private void checkOverridesAreReached(List<T> supers) {
		if (!supers.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))))
			rollbackAndThrowException(new IllegalStateException("Unable to reach overrides : " + supers + " for : " + info()));
	}

	private void checkDependsSuperComponents(List<T> supers) {
		getSupersStream().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), supers, getValue(), getComponents()))
				rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}

	@Override
	public Stream<T> getSupersStream() {
		return supers.stream();
	}

}
