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
	public static class MetaAttribute {
	}

	@Override
	public Vertex find(Class<?> clazz) {
		return systemCache.get(clazz);
	}

	// @Phantom
	@Override
	public Vertex getRoot() {
		return this;
	}

	// @Phantom
	@Override
	public Vertex getAlive() {
		return this;
	}

	private class SystemCache extends HashMap<Class<?>, Vertex> {

		private static final long serialVersionUID = 1150085123612887245L;

		private boolean startupTime = true;

		public SystemCache() {
			put(Root.class, Root.this);
		}

		void init(Class<?>... userClasses) {
			Vertex metaAttribute = Root.this.buildInstance(Collections.emptyList(), getValue(), Collections.singletonList(Root.this)).plug();
			systemCache.put(MetaAttribute.class, metaAttribute);

			Vertex map = metaAttribute.buildInstance(Collections.emptyList(), SystemMap.class, Collections.singletonList(Root.this)).plug();
			systemCache.put(SystemMap.class, map);
			map.enablePropertyConstraint();

			// List<Class<?>> classes = Arrays.<Class<?>> asList(MetaAttribute.class, SystemMap.class);
			// for (Class<?> clazz : classes)
			// get(clazz);
			for (Class<?> clazz : userClasses)
				get(clazz);
			startupTime = false;
		}

		public Vertex get(Class<?> clazz) {
			Vertex systemProperty = super.get(clazz);
			if (systemProperty != null) {
				assert systemProperty.isAlive();
				return systemProperty;
			}
			if (!startupTime)
				throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
			Vertex result;
			// TODO KK setInstance or buildInstance
			put(clazz, result = findMeta(clazz).setInstance(findOverrides(clazz), findValue(clazz), findComponents(clazz)));
			return result;
		}

		private Vertex findMeta(Class<?> clazz) {
			Meta meta = clazz.getAnnotation(Meta.class);
			return meta == null ? (Vertex) getRoot() : find(meta.value());
		}

		private List<Vertex> findOverrides(Class<?> clazz) {
			List<Vertex> overridesVertices = new ArrayList<Vertex>();
			org.genericsystem.kernel.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.kernel.annotations.Supers.class);
			if (supersAnnotation != null)
				for (Class<?> overrideClass : supersAnnotation.value())
					overridesVertices.add(find(overrideClass));
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

		private Vertex[] findComponents(Class<?> clazz) {
			List<Vertex> components = new ArrayList<Vertex>();
			Components componentsAnnotation = clazz.getAnnotation(Components.class);
			if (componentsAnnotation != null)
				for (Class<?> componentClass : componentsAnnotation.value())
					components.add(find(componentClass));
			return components.toArray(new Vertex[components.size()]);
		}
	}

}
