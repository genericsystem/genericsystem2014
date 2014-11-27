package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	private Archiver<Vertex> archiver;

	private final Context<Vertex> context;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(null, Collections.emptyList(), value, Collections.emptyList());

		context = new Context<Vertex>(this);
		context.init(new Checker<>(this), new AbstractBuilder<Vertex>(this) {

			@Override
			protected Vertex newT() {
				return new Vertex();
			}

			@Override
			protected Vertex[] newTArray(int dim) {
				return new Vertex[dim];
			}
		});

		Vertex metaAttribute = setMeta(Statics.ATTRIBUTE_SIZE);
		setMeta(Statics.RELATION_SIZE);
		setInstance(SystemMap.class, coerceToTArray(this)).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		if (persistentDirectoryPath != null) {
			archiver = new Archiver<Vertex>(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	// TODO mount this in API
	public void close() {
		if (archiver != null)
			archiver.close();
	}

	public static class MetaAttribute {
	}

}
