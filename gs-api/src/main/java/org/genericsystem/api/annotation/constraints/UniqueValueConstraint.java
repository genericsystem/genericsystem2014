package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.UniqueValueConstraintViolationException;

/**
 * Interface used to disallow instantiating a value several times in its context.
 * 
 * UniqueValueConstraint is positioned within a <tt>Generic</tt> to be exposed to its root Engine.
 * 
 * When the same value is instantiated, a <tt>UniqueValueConstraintViolationException</tt> is thrown.
 * 
 * @see UniqueValueConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UniqueValueConstraint {

}
