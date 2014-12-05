package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.constraints.InstanceValueClassConstraint;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.genericsystem.kernel.annotations.constraints.RequiredConstraint;
import org.genericsystem.kernel.annotations.constraints.SingularConstraint;
import org.genericsystem.kernel.annotations.constraints.UniqueValueConstraint;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.EngineValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;

public abstract class AbstractSystemCache<T extends AbstractVertex<T>> extends HashMap<Class<?>, T> {

	private static final long serialVersionUID = 1150085123612887245L;

	private boolean initialized = false;

	private final T root;

	public AbstractSystemCache(Class<?> rootClass, T root) {
		this.root = root;
		put(rootClass, root);
	}

	public abstract void mountConstraintsSystemClasses();

	public AbstractSystemCache<T> mount(List<Class<?>> systemClasses, Class<?>... userClasses) {
		for (Class<?> clazz : systemClasses)
			set(clazz);
		mountConstraintsSystemClasses();
		for (Class<?> clazz : userClasses)
			set(clazz);
		initialized = true;
		return this;
	}

	protected T set(Class<?> clazz) {
		if (initialized)
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		T systemProperty = super.get(clazz);
		if (systemProperty != null) {
			assert systemProperty.isAlive();
			return systemProperty;
		}
		T result = root.getCurrentCache().getBuilder().setInstance(clazz, setMeta(clazz), setOverrides(clazz), findValue(clazz), setComponents(clazz));
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
		if (meta == null)
			return root;
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
		BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
		if (booleanValue != null)
			return booleanValue.value();

		IntValue intValue = clazz.getAnnotation(IntValue.class);
		if (intValue != null)
			return intValue.value();

		StringValue stringValue = clazz.getAnnotation(StringValue.class);
		if (stringValue != null)
			return stringValue.value();

		EngineValue engineValue = clazz.getAnnotation(EngineValue.class);
		if (engineValue != null)
			return root.getValue();

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
