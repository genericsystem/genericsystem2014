package org.genericsystem.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.genericsystem.impl.annotations.Components;
import org.genericsystem.impl.annotations.Meta;
import org.genericsystem.impl.annotations.value.BooleanValue;
import org.genericsystem.impl.annotations.value.IntValue;
import org.genericsystem.impl.annotations.value.StringValue;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Root.MetaAttribute;
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
		put(MetaAttribute.class, root.setInstance(root, root.getValue(), root.coerceToArray(root)));
		put(SystemMap.class, root.setInstance(SystemMap.class, root.coerceToArray(root)).enablePropertyConstraint());
		for (Class<?> clazz : userClasses)
			set(clazz);
		initialized = true;
	}

	public T set(Class<?> clazz) {
		if (initialized)
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		T systemProperty = super.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		// TODO KK
		T result = setMeta(clazz).bindInstance(clazz, false, setOverrides(clazz), findValue(clazz), setComponents(clazz));
		put(clazz, result);
		return result;
	}

	private T setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return meta == null ? (T) root : set(meta.value());
	}

	private List<T> setOverrides(Class<?> clazz) {
		List<T> overridesVertices = new ArrayList<>();
		org.genericsystem.impl.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.impl.annotations.Supers.class);
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

	private List<T> setComponents(Class<?> clazz) {
		List<T> components = new ArrayList<>();
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation != null)
			for (Class<?> componentClass : componentsAnnotation.value())
				components.add(set(componentClass));
		return components;// TODO old version => root.coerceToArray(components.toArray());
	}

}
