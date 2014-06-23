package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.InstanceClassConstraintViolationException;

/**
 * <p>
 * Interface used to restrain the element to a class specified.
 * </p>
 * <p>
 * Throws a InstanceClassConstraintViolationException when trying to instantiate a InstanceClassConstraint element with an implementation which doesn't extends or implements the class specified.
 * </p>
 * 
 * @see InstanceClassConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface InstanceClassConstraint {

	/**
	 * Returns the class which restricts the target of InstanceClassConstraint.
	 * 
	 * @return the class which restricts the target of InstanceClassConstraint.
	 */
	Class<?> value();

}
