package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.AbstractBuilder.VertextBuilder;
import org.genericsystem.kernel.KernelConfig.MetaAttribute;
import org.genericsystem.kernel.KernelConfig.MetaRelation;
import org.genericsystem.kernel.KernelConfig.SystemMap;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final SystemCache<Vertex> systemCache;
	private final Archiver<Vertex> archiver;

	private final Context<Vertex> context;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(null, Collections.emptyList(), value, Collections.emptyList());

		context = new Context<>(this);
		context.init(new VertextBuilder(context));
		systemCache = new SystemCache<>(Root.class, this);
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);

		archiver = new Archiver<>(this, persistentDirectoryPath).startScheduler();
	}

	@Override
	public Vertex getMetaAttribute() {
		return getRoot().find(MetaAttribute.class);
	}

	@Override
	public Vertex getMetaRelation() {
		return getRoot().find(MetaRelation.class);
	}

	@Override
	public Vertex getMap() {
		return getRoot().find(SystemMap.class);
	}

	
	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Vertex> Custom find(Class<?> clazz) {
		return (Custom) systemCache.get(clazz);
	}

	// TODO mount this in API
	public void close() {
		archiver.close();
	}

}
