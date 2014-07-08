package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.kernel.exceptions.NotAliveException;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;

public abstract class Signature<T extends Signature<T>> {
	protected T meta;
	protected List<T> components;
	protected Serializable value;

	@SuppressWarnings("unchecked")
	protected T init(T meta, Serializable value, List<T> components) {
		if (meta != null) {
			meta.checkIsAlive();
			this.meta = meta;
		} else
			this.meta = (T) this;
		this.value = value;
		this.components = new ArrayList<>(components);
		for (int i = 0; i < components.size(); i++) {
			T component = components.get(i);
			if (component != null) {
				component.checkIsAlive();
				this.components.set(i, component);
			} else
				this.components.set(i, (T) this);
		}
		return (T) this;
	}

	public void checkIsAlive() {
		if (!((AncestorsService) this).isAlive())
			((ExceptionAdviserService<?>) this).rollbackAndThrowException(new NotAliveException(((DisplayService<?>) this).info()));
	}

	public T getMeta() {
		return meta;
	}

	public List<T> getComponents() {
		return components;
	}

	public Serializable getValue() {
		return value;
	}

	public Stream<T> getComponentsStream() {
		return components.stream();
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	public int getLevel() {
		return (((AncestorsService) this).isRoot() || components.stream().allMatch(c -> ((AncestorsService) c).isRoot()) && Objects.equals(getValue(), ((AncestorsService) this).getRoot().getValue())) ? 0 : meta.getLevel() + 1;
	}
}
