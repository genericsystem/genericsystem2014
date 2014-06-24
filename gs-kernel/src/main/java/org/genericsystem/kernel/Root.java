package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.AxedConstraintValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.kernel.exceptions.RollbackException;

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
			find(clazz, false);
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
		return find(clazz, true);
	}

	Vertex find(Class<?> clazz, boolean throwExceptionOnUnfoundClass) throws IllegalStateException {
		Vertex result = systemCache.get(clazz);
		if (result != null)
			return result;
		if (result == null && throwExceptionOnUnfoundClass)
			throw new RollbackException(new IllegalStateException());
		return new ClassFinder(clazz).find();
	}

	class ClassFinder {
		private final Class<?> clazz;

		public ClassFinder(Class<?> clazz) {
			this.clazz = clazz;
		}

		@SuppressWarnings("unchecked")
		public <T extends Vertex> T find() {
			Vertex result = systemCache.get(clazz);
			if (result == null)
				systemCache.put(clazz, result = findMeta().setInstance(findOverrides(), findValue(), findComponents()));
			return (T) result;
		}

		Vertex findMeta() {
			Meta meta = clazz.getAnnotation(Meta.class);
			return meta == null ? getRoot() : new ClassFinder(meta.value()).find();
		}

		List<Vertex> findOverrides() {
			List<Vertex> overridesVertices = new ArrayList<Vertex>();
			org.genericsystem.kernel.annotations.Supers overrides = clazz.getAnnotation(org.genericsystem.kernel.annotations.Supers.class);
			if (overrides != null) {
				List<Class<?>> overridesLst = Arrays.asList(overrides.value());
				overridesVertices = new ArrayList<Vertex>(overridesLst.size() + 1);
				for (Class<?> clazz : overridesLst) {
					overridesVertices.add(new ClassFinder(clazz).find());
				}
			}
			Class<?> javaSuperclass = clazz.getSuperclass();
			if (Object.class.equals(javaSuperclass) || clazz.getAnnotation(SystemGeneric.class) == null)
				return overridesVertices;
			ClassFinder classFinder = new ClassFinder(javaSuperclass);
			overridesVertices.add(classFinder.find());
			return overridesVertices;
		}

		Serializable findValue() {
			AxedConstraintValue axedConstraintValue = clazz.getAnnotation(AxedConstraintValue.class);
			if (axedConstraintValue != null) {
				Class<?> constraintClazz = axedConstraintValue.value();
				int axe = axedConstraintValue.axe();
				return "Constraint = " + constraintClazz + ", Axe = " + axe;
			}

			BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
			if (booleanValue != null) {
				boolean boolVal = booleanValue.value();
				return boolVal;
			}

			IntValue intValue = clazz.getAnnotation(IntValue.class);
			if (intValue != null) {
				int intVal = intValue.value();
				return intVal;
			}

			StringValue stringValue = clazz.getAnnotation(StringValue.class);
			if (stringValue != null) {
				String stringVal = stringValue.value();
				return stringVal;
			}
			return clazz;
		}

		Vertex[] findComponents() {
			Components components = clazz.getAnnotation(Components.class);
			if (components == null)
				return new Vertex[0];
			Class<?> componentsLst[] = components.value();
			Vertex componentsVertices[] = new Vertex[componentsLst.length];
			int index = 0;
			for (Class<?> clazz : componentsLst) {
				componentsVertices[index] = new ClassFinder(clazz).find();
				++index;
			}
			return componentsVertices;
		}
	}

}
