package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	private Archiver archiver;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		Vertex metaAttribut = setInstance(this, getValue(), coerceToTArray(this));
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribut.disableReferentialIntegrity(Statics.BASE_POSITION);
		if (persistentDirectoryPath != null) {
			archiver = new Archiver(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	@Override
	public Root getAlive() {
		return this;
	}

	@Override
	public Root getRoot() {
		return this;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	public void close() {
		if (archiver != null)
			archiver.close();
	}

	public static class MetaAttribute {
	}

}
