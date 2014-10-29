package org.genericsystem.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.genericsystem.cache.annotations.Components;
import org.genericsystem.cache.annotations.Meta;
import org.genericsystem.cache.annotations.value.BooleanValue;
import org.genericsystem.cache.annotations.value.IntValue;
import org.genericsystem.cache.annotations.value.StringValue;
import org.genericsystem.kernel.AbstractVertex.SystemMap;
import org.genericsystem.kernel.Root.MetaAttribute;

public class SystemCache<T extends AbstractGeneric<T, ?>> extends HashMap<Class<?>, T> {

	private static final long serialVersionUID = 1150085123612887245L;

	protected boolean initialized = false;

	private final T root;

	public SystemCache(T root) {
		this.root = root;
		put(Root.class, root);
	}

	public void init(Class<?>... userClasses) {
		put(MetaAttribute.class, root.setInstance(root, root.getValue(), root.coerceToTArray(root)));
		put(SystemMap.class, root.setInstance(SystemMap.class, root.coerceToTArray(root)).enablePropertyConstraint());
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
		org.genericsystem.cache.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.cache.annotations.Supers.class);
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
			for (Class<?> compositeClass : componentsAnnotation.value())
				components.add(set(compositeClass));
		return components;// root.coerceToArray(composites.toArray());
	}

}