package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.kernel.exceptions.NotAliveException;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;
import org.genericsystem.kernel.services.InheritanceService;

public abstract class ExtendedSignature<T extends InheritanceService<T>> extends Signature<T> {

	private T[] supers;

	protected T initFromOverrides(T meta, T[] overrides, Serializable value, @SuppressWarnings("unchecked") T... components) {
		return init(meta, () -> computeSupersCheckOverrides(overrides), value, components);
	}

	protected T initFromSupers(T meta, T[] supers, Serializable value, @SuppressWarnings("unchecked") T... components) {
		return init(meta, () -> supers, value, components);
	}

	@SuppressWarnings("unchecked")
	private T init(T meta, Supplier<T[]> supers, Serializable value, T... components) {
		super.init(meta, value, components);
		this.supers = supers.get();
		checkIsAlive(this.meta);
		checkAreAlive(this.supers);
		checkAreAlive(this.components);
		checkSupers();
		checkComponents();
		return (T) this;
	}

	public abstract T[] computeSupers(T[] overrides);

	public Stream<T> getSupersStream() {
		return Arrays.stream(supers);
	}

	private void checkAreAlive(@SuppressWarnings("unchecked") T... vertices) {
		Arrays.stream(vertices).forEach(this::checkIsAlive);
	}

	@SuppressWarnings("unchecked")
	private void checkIsAlive(T vertex) {
		if (!vertex.isAlive())
			((ExceptionAdviserService<T>) this).rollbackAndThrowException(new NotAliveException(((DisplayService<T>) vertex).info()));
	}

	private T[] computeSupersCheckOverrides(T[] overrides) {
		T[] supers = computeSupers(overrides);
		if (!Arrays.asList(overrides).stream().allMatch(override -> Arrays.stream(supers).anyMatch(superVertex -> superVertex.inheritsFrom(override))))
			throw new IllegalStateException("Inconsistant overrides : " + Arrays.toString(overrides) + " " + Arrays.stream(supers).collect(Collectors.toList()));
		return supers;
	}

	@SuppressWarnings("unchecked")
	private void checkSupers() {
		if (!getSupersStream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			((ExceptionAdviserService<T>) this).rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
		if (!getSupersStream().noneMatch(superVertex -> superVertex.equals(this)))
			((ExceptionAdviserService<T>) this).rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupersStream().collect(Collectors.toList())));
	}

	@SuppressWarnings("unchecked")
	private void checkComponents() {
		if (!(((InheritanceService<T>) this).componentsDepends(getComponents(), ((InheritanceService<T>) getMeta()).getComponents())))
			((ExceptionAdviserService<T>) this).rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		getSupersStream().forEach(superVertex -> {
			if (!(((InheritanceService<T>) this).componentsDepends(getComponents(), superVertex.getComponents())))
				((ExceptionAdviserService<T>) this).rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}
}
