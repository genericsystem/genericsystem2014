package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericsCache<T extends AbstractGeneric<T, ?>> {

	private final Map<T, T> map = new ConcurrentHashMap<>();
	static Logger log = LoggerFactory.getLogger(GenericsCache.class);

	private final T engine;

	public GenericsCache(T engine) {
		this.engine = engine;
	}

	@SuppressWarnings("unchecked")
	public <subT extends T> subT getOrBuildT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
		T disposable = (meta == null ? engine : meta).newT(clazz).init(meta, supers, value, composites);
		T result = map.get(disposable);
		if (result != null)
			return (subT) result;
		T alreadyPresent = map.putIfAbsent(disposable, disposable);
		return alreadyPresent != null ? (subT) alreadyPresent : (subT) disposable;
	}
}
