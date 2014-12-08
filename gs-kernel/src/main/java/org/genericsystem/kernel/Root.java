package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import org.genericsystem.kernel.AbstractBuilder.VertextBuilder;
import org.genericsystem.kernel.Root.DefaultNoReferentialIntegrityProperty.DefaultValue;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.value.AxedPropertyClassValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.EngineValue;
import org.genericsystem.kernel.systemproperty.NoReferentialIntegrityProperty;

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

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(Root.class)
	@Components(Root.class)
	@EngineValue
	@Dependencies({ DefaultNoReferentialIntegrityProperty.class })
	public static class MetaAttribute extends Vertex {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Root.class)
	@AxedPropertyClassValue(propertyClass = NoReferentialIntegrityProperty.class, pos = Statics.BASE_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultNoReferentialIntegrityProperty extends Vertex {

		@SystemGeneric
		@Meta(DefaultNoReferentialIntegrityProperty.class)
		@Components(MetaAttribute.class)
		@BooleanValue(true)
		public static class DefaultValue extends Vertex {

		}

	}

	@SystemGeneric
	@Meta(MetaRelation.class)
	@Supers(Root.class)
	@Components({ Root.class, Root.class })
	@EngineValue
	public static class MetaRelation extends Vertex {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Components(Root.class)
	@PropertyConstraint
	public static class SystemMap extends Vertex {}

	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <subT extends Vertex> subT find(Class<subT> clazz) {
		return (subT) systemCache.get(clazz);
	}

	// TODO mount this in API
	public void close() {
		archiver.close();
	}

}
