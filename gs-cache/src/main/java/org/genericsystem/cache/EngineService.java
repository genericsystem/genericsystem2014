package org.genericsystem.cache;


public interface EngineService<T extends GenericService<T>> extends org.genericsystem.impl.EngineService<T>, GenericService<T> {
	default Cache buildCache() {
		return new Cache();
	}

}
