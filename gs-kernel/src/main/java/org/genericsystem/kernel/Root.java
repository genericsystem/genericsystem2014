package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Root extends Vertex implements RootService<Vertex> {

	protected final static Logger log = LoggerFactory.getLogger(Root.class);

	private final SystemCache systemCache = new SystemCache();

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		systemCache.init(userClasses);
	}

	// TODO clean
	// @SystemGeneric
	// @Components(Root.class)
	// @StringValue(Statics.ENGINE_VALUE)
	public static class MetaAttribute {}

	@Override
	public Vertex find(Class<?> clazz) {
		return systemCache.get(clazz);
	}

	// @Phantom
	@Override
	public Root getRoot() {
		return this;
	}

	// @Phantom
	@Override
	public Root getAlive() {
		return this;
	}

	private class SystemCache extends HashMap<Class<?>, Vertex> {

		private static final long serialVersionUID = 1150085123612887245L;

		private boolean startupTime = true;

		private SystemCache() {
			put(Root.class, Root.this);
		}

		private void init(Class<?>... userClasses) {
			Vertex metaAttribute = Root.this.setInstance(Root.this, getValue(), Root.this);
			systemCache.put(MetaAttribute.class, metaAttribute);
			Vertex map = Root.this.setInstance(SystemMap.class, Root.this);
			systemCache.put(SystemMap.class, map);
			map.enablePropertyConstraint();
			for (Class<?> clazz : userClasses)
				set(clazz);
			startupTime = false;
		}

		private Vertex get(Class<?> clazz) {
			Vertex systemProperty = super.get(clazz);
			if (systemProperty != null) {
				assert systemProperty.isAlive();
				return systemProperty;
			}
			return null;
		}

		private Vertex set(Class<?> clazz) {
			if (!startupTime)
				throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
			Vertex systemProperty = super.get(clazz);
			if (systemProperty != null) {
				assert systemProperty.isAlive();
				return systemProperty;
			}
			Vertex result;
			put(clazz, result = setMeta(clazz).setInstance(setOverrides(clazz), findValue(clazz), setComponents(clazz)));
			return result;
		}

		private Vertex setMeta(Class<?> clazz) {
			Meta meta = clazz.getAnnotation(Meta.class);
			return meta == null ? (Vertex) getRoot() : set(meta.value());
		}

		private List<Vertex> setOverrides(Class<?> clazz) {
			List<Vertex> overridesVertices = new ArrayList<>();
			org.genericsystem.kernel.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.kernel.annotations.Supers.class);
			if (supersAnnotation != null)
				for (Class<?> overrideClass : supersAnnotation.value())
					overridesVertices.add(set(overrideClass));
			return overridesVertices;
		}

		private Serializable findValue(Class<?> clazz) {
			BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
			if (booleanValue != null)
				return booleanValue.value();

			IntValue intValue = clazz.getAnnotation(IntValue.class);
			if (intValue != null)
				return intValue.value();

			StringValue stringValue = clazz.getAnnotation(StringValue.class);
			if (stringValue != null)
				return stringValue.value();

			return clazz;
		}

		private Vertex[] setComponents(Class<?> clazz) {
			List<Vertex> components = new ArrayList<>();
			Components componentsAnnotation = clazz.getAnnotation(Components.class);
			if (componentsAnnotation != null)
				for (Class<?> componentClass : componentsAnnotation.value())
					components.add(set(componentClass));
			return components.toArray(new Vertex[components.size()]);
		}
	}

}
