package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	private boolean startupTime = true;

	private final T thisT;

	public SystemCache(T thisT) {
		this.thisT = thisT;
		put(Root.class, thisT);
	}

	public void init(Class<?>... userClasses) {
		T metaAttribute = thisT.setInstance(thisT, thisT.getValue(), thisT.coerceToArray(thisT));
		put(MetaAttribute.class, metaAttribute);

		T map = thisT.setInstance(SystemMap.class, thisT.coerceToArray(thisT));
		put(SystemMap.class, map);
		map.enablePropertyConstraint();
		for (Class<?> clazz : userClasses)
			set(clazz);
		startupTime = false;
	}

	public T get(Class<?> clazz) {
		T systemProperty = super.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		return null;
	}

	private T set(Class<?> clazz) {
		if (!startupTime)
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		T systemProperty = super.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		T result;
		put(clazz, result = setMeta(clazz).setInstance(setOverrides(clazz), findValue(clazz), setComponents(clazz)));
		return result;
	}

	@SuppressWarnings("unchecked")
	private T setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return meta == null ? (T) thisT.getRoot() : set(meta.value());
	}

	private List<T> setOverrides(Class<?> clazz) {
		List<T> overridesVertices = new ArrayList<>();
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

	private T[] setComponents(Class<?> clazz) {
		List<T> components = new ArrayList<>();
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation != null)
			for (Class<?> componentClass : componentsAnnotation.value())
				components.add(set(componentClass));
		return thisT.coerceToArray(components.toArray());
	}
}
