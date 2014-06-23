package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.SizeConstraintViolationException;

/**
 * <p>
 * Interface used to control the maximum number of components allowed.
 * </p>
 * <p>
 * Throws a <tt>SizeConstraintViolationException</tt> when the bound is reached.
 * </p>
 * 
 * @see SizeConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SizeConstraint {

	/**
	 * Returns the maximum number of components allowed.
	 * 
	 * @return the maximum number of components allowed.
	 */
	int[] value() default { 0 };
}
