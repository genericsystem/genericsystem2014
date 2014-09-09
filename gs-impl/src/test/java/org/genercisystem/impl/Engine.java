package org.genercisystem.impl;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.impl.EngineService;
import org.genericsystem.impl.SystemCache;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic, Engine> {

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
		return EngineService.super.getRoot();
	}

	@Override
	public Engine getAlive() {
		return (Engine) EngineService.super.getAlive();
	}

	@Override
	public boolean isRoot() {
		return EngineService.super.isRoot();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <subT extends Generic> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}
}
