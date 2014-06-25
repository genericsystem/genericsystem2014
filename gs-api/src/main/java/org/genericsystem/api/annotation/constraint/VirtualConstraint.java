package org.genericsystem.api.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.VirtualConstraintException;

/**
 * <p>
 * Interface used to feature the concept of abstraction.
 * </p>
 * <p>
 * <tt>VirtualConstraint</tt> should be implemented to be instantiated.
 * </p>
 * <p>
 * Throws a <tt>VirtualConstraintException</tt> when trying to instantiate a <tt>VirtualConstraint</tt> element.
 * </p>
 * 
 * @see VirtualConstraintException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface VirtualConstraint {

}
