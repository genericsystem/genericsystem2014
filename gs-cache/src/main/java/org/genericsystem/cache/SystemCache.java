package org.genericsystem.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.genericsystem.cache.annotations.Components;
import org.genericsystem.cache.annotations.Dependencies;
import org.genericsystem.cache.annotations.Meta;
import org.genericsystem.cache.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.cache.annotations.constraints.PropertyConstraint;
import org.genericsystem.cache.annotations.constraints.RequiredConstraint;
import org.genericsystem.cache.annotations.constraints.SingularConstraint;
import org.genericsystem.cache.annotations.constraints.UniqueValueConstraint;
import org.genericsystem.cache.annotations.value.BooleanValue;
import org.genericsystem.cache.annotations.value.IntValue;
import org.genericsystem.cache.annotations.value.StringValue;
import org.genericsystem.kernel.AbstractVertex.SystemMap;
import org.genericsystem.kernel.Root.MetaAttribute;
import org.genericsystem.kernel.Statics;

public class SystemCache<T extends AbstractGeneric<T>> extends HashMap<Class<?>, T> {

	private static final long serialVersionUID = 1150085123612887245L;

	protected boolean initialized = false;

	private final T engine;

	public SystemCache(T engine) {
		this.engine = engine;
		put(Engine.class, engine);
	}

	public void init(Class<?>... userClasses) {
		put(MetaAttribute.class, engine.setInstance(engine, engine.getValue(), engine.coerceToTArray(engine)));
		put(SystemMap.class, engine.setInstance(SystemMap.class, engine.coerceToTArray(engine)).enablePropertyConstraint());
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
		T meta = setMeta(clazz);
		T result = engine.getCurrentCache().getBuilder().setInstance(clazz, meta, setOverrides(clazz), findValue(clazz), meta.coerceToTArray(setComponents(clazz).toArray()));
		put(clazz, result);
		mountConstraints(result, clazz);
		triggersDependencies(clazz);
		return result;
	}

	private void triggersDependencies(Class<?> clazz) {
		Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
		if (dependenciesClass != null)
			for (Class<?> dependencyClass : dependenciesClass.value())
				set(dependencyClass);
	}

	private void mountConstraints(T result, Class<?> clazz) {

		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			result.enablePropertyConstraint();

		if (clazz.getAnnotation(UniqueValueConstraint.class) != null)
			result.enableUniqueValueConstraint();

		if (clazz.getAnnotation(InstanceValueClassConstraint.class) != null)
			result.setClassConstraint(clazz.getAnnotation(InstanceValueClassConstraint.class).value());

		if (clazz.getAnnotation(RequiredConstraint.class) != null)
			result.enableRequiredConstraint(Statics.NO_POSITION);

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				result.enableSingularConstraint(axe);

	}

	private T setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return meta == null ? (T) engine : set(meta.value());
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
				if (compositeClass.equals(clazz))
					components.add(null);
				else
					components.add(set(compositeClass));
		return components;
	}
}
