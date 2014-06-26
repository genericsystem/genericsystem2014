package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;

public class Root extends Vertex implements RootService<Vertex> {

	final Map<Class<?>, Vertex> systemCache = new HashMap<Class<?>, Vertex>();

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		systemCache.put(Root.class, this);
		initSystemMap();
		for (Class<?> clazz : userClasses)
			find(clazz);
	}

	public void initSystemMap() {
		Vertex map = buildInstance(Collections.emptyList(), SystemMap.class, Collections.singletonList(this)).plug();
		systemCache.put(SystemMap.class, map);
		map.enablePropertyConstraint();
	}

	@Override
	public Vertex getRoot() {
		return this;
	}

	@Override
	public Vertex getAlive() {
		return this;
	}

	@Override
	public Vertex find(Class<?> clazz) {
		Vertex result = systemCache.get(clazz);
		if (result != null)
			return result;
		if (result == null)
			systemCache.put(clazz, result = findMeta(clazz).setInstance(findOverrides(clazz), findValue(clazz), findComponents(clazz)));
		return result;
	}

	Vertex findMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return meta == null ? getRoot() : find(meta.value());
	}

	List<Vertex> findOverrides(Class<?> clazz) {
		List<Vertex> overridesVertices = new ArrayList<Vertex>();
		org.genericsystem.kernel.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.kernel.annotations.Supers.class);
		if (supersAnnotation != null)
			for (Class<?> overrideClass : supersAnnotation.value())
				overridesVertices.add(find(overrideClass));
		return overridesVertices;
	}

	Serializable findValue(Class<?> clazz) {
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

	Vertex[] findComponents(Class<?> clazz) {
		List<Vertex> components = new ArrayList<Vertex>();
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation != null)
			for (Class<?> componentClass : componentsAnnotation.value())
				components.add(find(componentClass));
		return components.toArray(new Vertex[components.size()]);
	}
}
