package org.genericsystem.api.core.annotations.constraints;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.genericsystem.api.core.IVertex;

/**
 * The generator to compute the instances values.
 * 
 * @author Nicolas Feybesse
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface InstanceValueGenerator {
	/**
	 * The class of instance values generator.
	 * 
	 * @return the class of instance values generator.
	 */
	Class<? extends ValueGenerator> value() default DefaultInstanceValueGenerator.class;

	public static interface ValueGenerator<T extends IVertex<T>> {
		/**
		 * @param meta
		 * @param supers
		 * @param value
		 * @param components
		 * @return the generate value for new instances
		 */
		Serializable generateInstanceValue(T meta, List<T> supers, Serializable value, List<T> components);
	}

	public static class DefaultInstanceValueGenerator<T extends IVertex<T>> implements ValueGenerator<T> {
		/**
		 * @return the generate value for new instances
		 */
		@Override
		public Serializable generateInstanceValue(T meta, List<T> supers, Serializable value, List<T> components) {
			return components.size() > 0 ? components.toString() : (int) (Math.random() * Integer.MAX_VALUE);
		};
	}
}