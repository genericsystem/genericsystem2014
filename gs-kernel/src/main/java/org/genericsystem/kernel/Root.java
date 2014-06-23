package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.genericsystem.kernel.annotations.Meta;

public class Root extends Vertex implements RootService<Vertex> {

	public Root() {
		this(Statics.ENGINE_VALUE);
	}

	public Root(Serializable value) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
	}

	@Override
	public Vertex getRoot() {
		return this;
	}

	@Override
	public Vertex getAlive() {
		return this;
	}

	private Map<Class<?>, Vertex> systemCache = new HashMap<Class<?>, Vertex>();

	public Vertex find(Class<?> clazz) {
		Vertex result = systemCache.get(clazz);
		return result == null ? result = findMeta(clazz).setInstance(findOverrides(clazz), findValue(clazz), findComponents(clazz)) : result;
	}

	Vertex findMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return find(meta.value());
	}

	List<Vertex> findOverrides(Class<?> clazz) {
		return null;
	}

	Serializable findValue(Class<?> clazz) {
		return null;
	}

	Vertex[] findComponents(Class<?> clazz) {
		return null;
	}
}
