package org.genericsystem.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Root.MetaAttribute;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.kernel.services.MapService.SystemMap;
import org.genericsystem.kernel.services.VertexService;

public class SystemCache<T extends VertexService<T, ?>> extends HashMap<Class<?>, T> {

	private static final long serialVersionUID = 1150085123612887245L;

	protected boolean initialized = false;

	private final T root;

	public SystemCache(T root) {
		this.root = root;
		put(Root.class, root);
	}

	public void init(Class<?>... userClasses) {
		T metaAttribute = root.setInstance(root, root.getValue(), root.coerceToArray(root));
		put(MetaAttribute.class, metaAttribute);

		T map = root.setInstance(SystemMap.class, root.coerceToArray(root));
		put(SystemMap.class, map);
		map.enablePropertyConstraint();
		for (Class<?> clazz : userClasses)
			set(clazz);
		initialized = true;
		assert map.isAlive();
	}

	public T get(Class<?> clazz) {
		T systemProperty = super.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive() : systemProperty.info();
			return systemProperty;
		}
		return null;
	}

	public T set(Class<?> clazz) {
		if (initialized)
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		T systemProperty = super.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		T result = setMeta(clazz).setInstance(setOverrides(clazz), findValue(clazz), setComponents(clazz));
		put(clazz, result);
		return result;
	}

	private T setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return meta == null ? (T) root : set(meta.value());
	}

	private List<T> setOverrides(Class<?> clazz) {
		List<T> overridesVertices = new ArrayList<>();
		org.genericsystem.kernel.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.kernel.annotations.Supers.class);
		if (supersAnnotation != null)
			for (Class<?> overrideClass : supersAnnotation.value())
				overridesVertices.add(set(overrideClass));
		return overridesVertices;
	}

	private static Serializable findValue(Class<?> clazz) {
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

	private T[] setComponents(Class<?> clazz) {
		List<T> components = new ArrayList<>();
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation != null)
			for (Class<?> componentClass : componentsAnnotation.value())
				components.add(set(componentClass));
		return root.coerceToArray(components.toArray());
	}

}