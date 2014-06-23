package org.genericsystem.api.annotation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.PropertyConstraintViolationException;

/**
 * <p>
 * Interface used to propagate a value to all the components on the same "axis".
 * </p>
 * <p>
 * GenericSystem could be represented as a graph. When instantiating a new node, an axis is created. All the dependencies of this node will be on the same axis.
 * </p>
 * <p>
 * Throws a <tt>PropertyConstraintViolationException</tt> when trying to instantiate a new relation with a different value on the bound where <tt>PropertyConstraint</tt> is positioned.
 * </p>
 * 
 * @see PropertyConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PropertyConstraint {

}
