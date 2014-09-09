package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericsCache<T extends AbstractGeneric<T, ?, ?, ?>> {

	private final Map<T, T> map = new ConcurrentHashMap<>();
	static Logger log = LoggerFactory.getLogger(GenericsCache.class);

	public <subT extends T> subT getOrBuildT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		log.info("start find generic : " + meta + " " + value + " with supers " + supers);
		T disposable = meta.newInstance(clazz).init(throwExistException, meta, supers, value, components);
		T result = map.get(disposable);
		if (result == null) {
			log.info("Not found");
			result = disposable;
			T alreadyPresent = map.putIfAbsent(result, result);
			if (alreadyPresent != null) {
				result = alreadyPresent;
				log.info("finally found generic : " + result.info() + " with supers " + supers);
			} else
				log.info("Build new generic : " + result.info());
		} else
			log.info("found generic : " + result.info() + " with supers " + supers);
		assert result != null;
		return (subT) result;
	}
}