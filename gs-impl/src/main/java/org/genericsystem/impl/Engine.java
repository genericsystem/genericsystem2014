package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Collections;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements IEngine<Generic, Engine> {

	private final Root root;

	private final SystemCache<Generic> systemCache = new SystemCache<>(this);

	public Engine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		root = buildRoot(engineValue);
		init(false, null, Collections.emptyList(), engineValue, Collections.emptyList());
		systemCache.init(userClasses);
	}

	@SuppressWarnings("static-method")
	Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	protected Vertex unwrap() {
		return root;
	}

	@Override
	public Engine getRoot() {
		return this;
	}

	@Override
	public Engine getAlive() {
		return this;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}
}
