package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.RequiredConstraintViolationException;

/**
 * <p>
 * Interface used to require the element(s) targeted when instantiating the source of RequiredConstraint.
 * </p>
 * <p>
 * Throws a <tt>RequiredConstraintViolationException</tt> the source of RequiredConstraint is instantiated without the element(s) targeted.
 * </p>
 * 
 * @see RequiredConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface RequiredConstraint {

	/**
	 * Returns the positions of the generics which should be linked when instantiating the source of RequiredConstraint.
	 * 
	 * @return the positions of the generics which should be linked when instantiating the source of RequiredConstraint.
	 */
	int[] value() default {};
}
