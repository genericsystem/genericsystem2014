package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Extends;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.value.AxedConstraintValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.kernel.exceptions.RollbackException;

public class Root extends Vertex implements RootService<Vertex> {

	private final Map<Class<?>, Vertex> systemCache = new HashMap<Class<?>, Vertex>();

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
			systemCache.put(clazz, result);
		}
		return result;
	}

	class ClassFinder {

		private final Class<?> clazz;

		public ClassFinder(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Vertex find() {
			return findMeta().setInstance(findOverrides(), findValue(), findComponents());
		}

		Vertex findMeta() {
			Meta meta = clazz.getAnnotation(Meta.class);
			if (meta == null)
				return getRoot();
			return (new ClassFinder(meta.value())).find();
		}

		List<Vertex> findOverrides() {
			Extends overrides = clazz.getAnnotation(Extends.class);
			if (overrides == null)
				return Collections.emptyList();
			List<Class<?>> overridesLst = Arrays.asList(overrides.value());
			List<Vertex> overridesVertices = new ArrayList<Vertex>(overridesLst.size());
			for (Class<?> clazz : overridesLst) {
				overridesVertices.add(new ClassFinder(clazz).find());
			}
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
