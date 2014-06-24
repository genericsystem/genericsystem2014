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
		for (Class<?> clazz : userClasses)
			find(clazz, false);
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
		if (result == null && throwExceptionOnUnfoundClass)
			throw new RollbackException(new IllegalStateException());
		else {
			ClassFinder classFinder = new ClassFinder(clazz);
			result = classFinder.find();
		}
		return result;
	}

	class ClassFinder {

		private final Class<?> clazz;

		// private final Map<Class<?>, Vertex> systemCache;

		public ClassFinder(Class<?> clazz) {
			// this.systemCache = systemCache;
			this.clazz = clazz;
		}

		public <T extends Vertex> T find() {
			T result = (T) findMeta().setInstance(findOverrides(), findValue(), findComponents());
			Root.this.systemCache.put(clazz, result);
			return result;
		}

		Vertex findMeta() {
			Meta meta = clazz.getAnnotation(Meta.class);
			if (meta == null || meta.value() == Root.class)
				return getRoot();
			return (new ClassFinder(meta.value())).find();
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
