package org.genericsystem.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.ConcreteInheritanceConstraintViolationException;

/**
 * <p>
 * Interface used to disallow inheritance. An instance of <tt>NoInheritance<tt> will not be able to have inheritance.
 * </p>
 * <p>
 * Throws a <tt>ConcreteInheritanceConstraintViolationException</tt> when trying to add a super to a <tt>NoInheritance</tt> element.
 * </p>
 * 
 * @see ConcreteInheritanceConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface NoInheritance {

}
