package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.genericsystem.api.exception.CyclicException;
import org.genericsystem.kernel.GenericHandler.SetSystemHandler;
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

public class SystemCache {

	private final Map<Class<?>, Generic> systemCache = new HashMap<>();

	private final Map<Generic, Class<?>> reverseSystemCache = new IdentityHashMap<>();

	protected final Root root;

	public SystemCache(Root root, Class<?> rootClass) {
		this.root = root;
		put(Root.class, root);
		put(rootClass, root);
	}

	public void mount(List<Class<?>> systemClasses, Class<?>... userClasses) {
		for (Class<?> clazz : systemClasses)
			set(clazz);
		for (Class<?> clazz : userClasses)
			set(clazz);
	}

	private Generic set(Class<?> clazz) {
		if (root.isInitialized())
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		Generic systemProperty = systemCache.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		Generic meta = setMeta(clazz);
		List<Generic> overrides = setOverrides(clazz);
		List<Generic> components = setComponents(clazz);
		Generic result = new SetSystemHandler<>(((Generic) root).getCurrentCache(), clazz, meta, overrides, findValue(clazz), components).resolve();
		put(clazz, result);
		mountConstraints(clazz, result);
		triggersDependencies(clazz);
		return result;
	}

	private void put(Class<?> clazz, Generic vertex) {
		systemCache.put(clazz, vertex);
		reverseSystemCache.put(vertex, clazz);
	}

	public Generic get(Class<?> clazz) {
		return systemCache.get(clazz);
	}

	public Class<?> getByVertex(Generic vertex) {
		return reverseSystemCache.get(vertex);
	}

	void mountConstraints(Class<?> clazz, Generic result) {
		if (clazz.getAnnotation(PropertyConstraint.class) != null)
			result.enablePropertyConstraint();

		if (clazz.getAnnotation(UniqueValueConstraint.class) != null)
			result.enableUniqueValueConstraint();

		if (clazz.getAnnotation(InstanceValueClassConstraint.class) != null)
			result.setClassConstraint(clazz.getAnnotation(InstanceValueClassConstraint.class).value());

		RequiredConstraint requiredConstraint = clazz.getAnnotation(RequiredConstraint.class);
		if (requiredConstraint != null)
			for (int axe : requiredConstraint.value())
				result.enableRequiredConstraint(axe);

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

	private Generic setMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		if (meta == null)
			return root;
		if (meta.value() == clazz)
			return null;
		return set(meta.value());
	}

	private List<Generic> setOverrides(Class<?> clazz) {
		List<Generic> overridesVertices = new ArrayList<>();
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

	private List<Generic> setComponents(Class<?> clazz) {
		List<Generic> components = new ArrayList<>();
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
