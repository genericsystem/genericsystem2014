package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.exception.CyclicException;
import org.genericsystem.kernel.GenericHandler.SetHandler;
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

	private final Map<Class<?>, T> systemCache = new HashMap<>();

	private final Map<T, Class<?>> reverseSystemCache = new IdentityHashMap<>();

	protected final DefaultRoot<T> root;

	@SuppressWarnings("unchecked")
	public SystemCache(DefaultRoot<T> root, Class<?> rootClass) {
		this.root = root;
		put(rootClass, (T) root);
	}

	public void mount(List<Class<?>> systemClasses, Class<?>... userClasses) {
		for (Class<?> clazz : systemClasses)
			set(clazz);
		for (Class<?> clazz : userClasses)
			set(clazz);
	}

	@SuppressWarnings("unchecked")
	private T set(Class<?> clazz) {
		if (root.isInitialized())
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		T systemProperty = systemCache.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		T meta = setMeta(clazz);
		List<T> overrides = setOverrides(clazz);
		List<T> components = setComponents(clazz);
		T result = new SetHandler<>(((T) root).getCurrentCache(), clazz, meta != null ? meta : (T) root, overrides, findValue(clazz), components).resolve();
		// Builder<T> builder = ((T) root).getCurrentCache().getBuilder();
		// if (meta == null) {
		// assert overrides.size() == 1;
		// } else {
		// if (meta.isMeta())
		// meta = ((T) root).getCurrentCache().setMeta(components.size());
		// }
		// result = builder.buildAndPlug(clazz, meta, overrides, findValue(clazz), components);

		put(clazz, result);
		mountConstraints(clazz, result);
		triggersDependencies(clazz);
		return result;
	}

	private void put(Class<?> clazz, T vertex) {
		systemCache.put(clazz, vertex);
		reverseSystemCache.put(vertex, clazz);
	}

	public T get(Class<?> clazz) {
		return systemCache.get(clazz);
	}

	public Class<?> getByVertex(T vertex) {
		return reverseSystemCache.get(vertex);
	}

	void mountConstraints(Class<?> clazz, T result) {
		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			result.enablePropertyConstraint();

		if (clazz.getAnnotation(UniqueValueConstraint.class) != null)
			result.enableUniqueValueConstraint();

		if (clazz.getAnnotation(InstanceValueClassConstraint.class) != null)
			result.setClassConstraint(clazz.getAnnotation(InstanceValueClassConstraint.class).value());

		if (clazz.getAnnotation(RequiredConstraint.class) != null)
			result.enableRequiredConstraint(ApiStatics.NO_POSITION);

		NoReferentialIntegrityProperty referentialIntegrity = clazz.getAnnotation(NoReferentialIntegrityProperty.class);
		if (referentialIntegrity != null)
			for (int axe : referentialIntegrity.value())
				result.disableReferentialIntegrity(axe);

		SingularConstraint singularTarget = clazz.getAnnotation(SingularConstraint.class);
		if (singularTarget != null)
			for (int axe : singularTarget.value())
				result.enableSingularConstraint(axe);
	}

	private void triggersDependencies(Class<?> clazz) {
		Dependencies dependenciesClass = clazz.getAnnotation(Dependencies.class);
		if (dependenciesClass != null)
			for (Class<?> dependencyClass : dependenciesClass.value())
				set(dependencyClass);
	}

	@SuppressWarnings("unchecked")
	private T setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		if (meta == null)
			return (T) root;
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
					root.getCurrentCache().discardWithException(new CyclicException("The annoted class " + clazz + " has a component with same name"));
				else
					components.add(set(compositeClass));
		return components;
	}
}
