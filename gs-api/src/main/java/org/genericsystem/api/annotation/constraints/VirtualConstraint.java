package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.VirtualConstraintException;

/**
 * Interface used to feature the concept of abstraction.
 * 
 * VirtualConstraint should be implemented to be instantiated.
 * 
 * When trying to instantiate a VirtualConstraint element, a <tt>VirtualConstraintException</tt> is thrown.
 * 
 * @see VirtualConstraintException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface VirtualConstraint {

}
