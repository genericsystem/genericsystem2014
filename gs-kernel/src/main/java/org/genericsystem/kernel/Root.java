package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.kernel.AbstractBuilder.VertextBuilder;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.MetaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	private final SystemCache<Vertex> systemCache = new SystemCache<>(this);

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
		context.init(new VertextBuilder(context));

		Vertex metaAttribute = systemCache.set(MetaAttribute.class);
		systemCache.set(MetaRelation.class);
		systemCache.set(SystemMap.class).enablePropertyConstraint();
		metaAttribute.disableReferentialIntegrity(Statics.BASE_POSITION);
		if (persistentDirectoryPath != null) {
			archiver = new Archiver<Vertex>(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	@SystemGeneric
	@Supers(Root.class)
	@Components(Root.class)
	@MetaValue
	public static class MetaAttribute extends Vertex {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Root.class)
	@Components({ Root.class, Root.class })
	@MetaValue
	public static class MetaRelation extends Vertex {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Root.class)
	public static class SystemMap extends Vertex {
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

}
