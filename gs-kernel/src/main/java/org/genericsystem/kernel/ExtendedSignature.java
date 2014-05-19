package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.exceptions.NotAliveException;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;
import org.genericsystem.kernel.services.InheritanceService;

public abstract class ExtendedSignature<T extends InheritanceService<T>> extends Signature<T> implements InheritanceService<T>, ExceptionAdviserService<T> {

	private List<T> supers;

	public T initFromOverrides(T meta, List<T> overrides, Serializable value, List<T> components) {
		return init(meta, () -> computeSupersCheckOverrides(overrides), value, components);
	}

	public T initFromSupers(T meta, List<T> supers, Serializable value, List<T> components) {
		return init(meta, () -> supers, value, components);
	}

	@SuppressWarnings("unchecked")
	private T init(T meta, Supplier<List<T>> supersSupplier, Serializable value, List<T> components) {
		super.init(meta, value, components);
		this.supers = supersSupplier.get();
		checkIsAlive(this.meta);
		checkAreAlive(this.supers);
		checkAreAlive(this.components);
		checkSupers();
		checkComponents();
		return (T) this;
	}

	@Override
	public Stream<T> getSupersStream() {
		return supers.stream();
	}

	private void checkAreAlive(List<T> vertices) {
		vertices.forEach(this::checkIsAlive);
	}

	@SuppressWarnings("unchecked")
	private void checkIsAlive(T vertex) {
		if (!vertex.isAlive())
			rollbackAndThrowException(new NotAliveException(((DisplayService<T>) vertex).info()));
	}

	private List<T> computeSupersCheckOverrides(List<T> overrides) {
		List<T> supers = computeSupers(overrides);
		if (!overrides.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override))))
			throw new IllegalStateException("Inconsistant overrides : " + overrides + " " + supers);
		return supers;
	}

	private void checkSupers() {
		if (!getSupersStream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!getSupersStream().noneMatch(superVertex -> superVertex.equals(this)))
			rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
	}

	private void checkComponents() {
		if (!(componentsDepends(getComponents(), getMeta().getComponents())))
			((ExceptionAdviserService<T>) this).rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		getSupersStream().forEach(superVertex -> {
			if (!(componentsDepends(getComponents(), superVertex.getComponents())))
				rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}
}
