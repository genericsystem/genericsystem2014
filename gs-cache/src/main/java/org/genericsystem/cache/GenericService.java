package org.genericsystem.cache;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {
	default Cache getCurrentCache() {
		return getMeta().getCurrentCache();
	}
}