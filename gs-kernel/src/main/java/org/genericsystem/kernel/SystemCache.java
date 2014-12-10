package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.NoReferentialIntegrityProperty;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.constraints.RequiredConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.kernel.annotations.constraints.UniqueValueConstraint;
import org.genericsystem.kernel.annotations.value.AxedPropertyClassValue;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.ByteArrayValue;
import org.genericsystem.kernel.annotations.value.DoubleValue;
import org.genericsystem.kernel.annotations.value.EngineValue;
import org.genericsystem.kernel.annotations.value.FloatValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.LongValue;
import org.genericsystem.kernel.annotations.value.ShortValue;
import org.genericsystem.kernel.annotations.value.StringValue;

public class SystemCache<T extends AbstractVertex<T>> {

	private Map<Class<?>, T> systemCache = new HashMap<>();

	private boolean initialized = false;

	protected final DefaultRoot<T> root;

	@SuppressWarnings("unchecked")
	public SystemCache(DefaultRoot<T> root, Class<?> rootClass) {
		this.root = root;
		systemCache.put(rootClass, (T)root);
	}

	public void mount(List<Class<?>> systemClasses, Class<?>... userClasses) {
		for (Class<?> clazz : systemClasses)
			set(clazz);
		for (Class<?> clazz : userClasses)
			set(clazz);
		initialized = true;
	}

	private T set(Class<?> clazz) {
		if (initialized)
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		T systemProperty = systemCache.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		T result = root.getCurrentCache().getBuilder().setInstance(clazz, setMeta(clazz), setOverrides(clazz), findValue(clazz), setComponents(clazz));
		systemCache.put(clazz, result);
		mountConstraints(clazz, result);
		triggersDependencies(clazz);
		return result;
	}
	
	public T get(Class<?> clazz){
		return systemCache.get(clazz);
	}

	private void triggersDependencies(Class<?> clazz) {
		Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
		if (dependenciesClass != null)
			for (Class<?> dependencyClass : dependenciesClass.value())
				set(dependencyClass);
	}

	private void mountConstraints(Class<?> clazz, T result) {
		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			result.enablePropertyConstraint();

		if (clazz.getAnnotation(UniqueValueConstraint.class) != null)
			result.enableUniqueValueConstraint();

		if (clazz.getAnnotation(InstanceValueClassConstraint.class) != null)
			result.setClassConstraint(clazz.getAnnotation(InstanceValueClassConstraint.class).value());

		if (clazz.getAnnotation(RequiredConstraint.class) != null)
			result.enableRequiredConstraint(Statics.NO_POSITION);

		NoReferentialIntegrityProperty referentialIntegrity = clazz.getAnnotation(NoReferentialIntegrityProperty.class);
		if (referentialIntegrity != null)
			for (int axe : referentialIntegrity.value())
				result.disableReferentialIntegrity(axe);

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				result.enableSingularConstraint(axe);
	}

	@SuppressWarnings("unchecked")
	private T setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		if (meta == null)
			return (T)root;
		if (meta.value() == clazz)
			return null;
		return set(meta.value());
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
		AxedPropertyClassValue axedPropertyClass = clazz.getAnnotation(AxedPropertyClassValue.class);
		if (axedPropertyClass != null)
			return new org.genericsystem.kernel.systemproperty.AxedPropertyClass(axedPropertyClass.propertyClass(), axedPropertyClass.pos());

		BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
		if (booleanValue != null)
			return booleanValue.value();

		ByteArrayValue byteArrayValue = clazz.getAnnotation(ByteArrayValue.class);
		if (byteArrayValue != null)
			return byteArrayValue.value();

		DoubleValue doubleValue = clazz.getAnnotation(DoubleValue.class);
		if (doubleValue != null)
			return doubleValue.value();

		EngineValue engineValue = clazz.getAnnotation(EngineValue.class);
		if (engineValue != null)
			return root.getValue();

		FloatValue floatValue = clazz.getAnnotation(FloatValue.class);
		if (floatValue != null)
			return floatValue.value();

		IntValue intValue = clazz.getAnnotation(IntValue.class);
		if (intValue != null)
			return intValue.value();

		LongValue longValue = clazz.getAnnotation(LongValue.class);
		if (longValue != null)
			return longValue.value();

		ShortValue shortValue = clazz.getAnnotation(ShortValue.class);
		if (shortValue != null)
			return shortValue.value();

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
