package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.kernel.services.ApiService;
import org.genericsystem.kernel.services.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements RootService<Vertex, Root> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	// private final SystemCache<Vertex> systemCache = new SystemCache<>(this);

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		Root root = getRoot();
		root.setInstance(root, root.getValue(), coerceToArray(root));
		Vertex map = root.setInstance(SystemMap.class, root.coerceToArray(root));
		map.enablePropertyConstraint();
		assert map.isAlive();
	}

	@Override
	public Root getRoot() {
		return RootService.super.getRoot();
	}

	@Override
	public Root getAlive() {
		return (Root) RootService.super.getAlive();
	}

	@Override
	public boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		return RootService.super.equiv(service);
	}

	@Override
	public boolean isRoot() {
		return RootService.super.isRoot();
	}

	// TODO clean
	// @SystemGeneric
	// @Components(Root.class)
	// @StringValue(Statics.ENGINE_VALUE)
	public static class MetaAttribute {
	}

}
