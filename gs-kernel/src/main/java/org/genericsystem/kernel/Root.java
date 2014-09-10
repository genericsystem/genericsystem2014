package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements IRoot<Vertex, Root> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		setInstance(this, getValue(), coerceToArray(this));
		Vertex map = setInstance(SystemMap.class, coerceToArray(this)).enablePropertyConstraint();
		assert map.isAlive();
	}

	public static class MetaAttribute {
	}

}
