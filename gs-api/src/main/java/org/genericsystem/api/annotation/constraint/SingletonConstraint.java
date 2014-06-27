package org.genericsystem.api.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.SingletonConstraintViolationException;

/**
 * <p>
 * Interface used to feature the concept of Singleton. Restricts the instantiation of a class to one object.
 * </p>
 * <p>
 * Throws a <tt>SingletonConstraintViolationException</tt> when trying to instantiate a <tt>SingularConstraint</tt> element which has already been instantiated.
 * </p>
 * 
 * @see SingletonConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SingletonConstraint {

}