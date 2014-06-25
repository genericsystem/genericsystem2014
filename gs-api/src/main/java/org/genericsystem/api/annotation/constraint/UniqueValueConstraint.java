package org.genericsystem.api.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.UniqueValueConstraintViolationException;

/**
 * <p>
 * Interface used to disallow instantiating a value several times in its context.
 * </p>
 * <p>
 * <tt>UniqueValueConstraint</tt> is positioned within a <tt>Generic</tt> to be exposed to its root <tt>Engine</tt>.
 * </p>
 * <p>
 * When the same value is instantiated, a <tt>UniqueValueConstraintViolationException</tt> is thrown.
 * </p>
 * 
 * @see UniqueValueConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UniqueValueConstraint {

}
