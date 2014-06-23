package org.genericsystem.kernel.services;

public interface ImmutablesService<T extends ImmutablesService<T>> extends AncestorsService<T> {
	default T find(Class<?> clazz) {
		return getRoot().find(clazz);
	}
}
