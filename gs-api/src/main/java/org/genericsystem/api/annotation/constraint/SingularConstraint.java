package org.genericsystem.api.annotation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.genericsystem.api.exception.SingularConstraintViolationException;

/**
 * <p>
 * Interface used to propagate a value to all the components on the same "axis".
 * </p>
 * <p>
 * GenericSystem could be represented as a graph. When instantiating a new node, an axis is created. All the dependencies of this node will be on the same axis.
 * </p>
 * <p>
 * For example, two <tt>Generic</tt> are instantiated: Vehicle and Color. Then is instantiated a relation between vehicle and color. A <tt>SingularConstraint</tt> positioned on Color would mean every Vehicle would be of the same color : the one
 * instantiated on the relation.
 * </p>
 * <p>
 * Throws a <tt>SingularConstraintViolationException</tt> when trying to instantiate a new relation with a different value on the bound where <tt>SingularConstraint</tt> is positioned.
 * </p>
 * 
 * @see SingularConstraintViolationException
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SingularConstraint {

	/**
	 * Returns the axis number where the components will be restrained by the SingularConstraint.
	 * 
	 * @return the axis number where the components will be restrained by the SingularConstraint
	 */
	int[] value() default { 0 };

}
