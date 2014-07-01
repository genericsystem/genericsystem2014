package org.genericsystem.api.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.SizeConstraintViolationException;
import org.genericsystem.api.model.Holder;

/**
 * <p>
 * Interface used to control the maximum number of components allowed.
 * </p>
 * <p>
 * Throws a <tt>SizeConstraintViolationException</tt> when adding more <tt>Holder</tt> than the number specified by the Constraint to the element where the constraint is positioned.
 * </p>
 * 
 * @see Holder
 * @see SizeConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SizeConstraint {

	/**
	 * Returns the maximum number of <tt>Holder</tt> allowed.
	 * 
	 * @return the maximum number of <tt>Holder</tt> allowed
	 */
	int[] value() default { 0 };
}
