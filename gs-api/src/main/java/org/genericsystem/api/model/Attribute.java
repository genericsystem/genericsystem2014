package org.genericsystem.api.model;

import org.genericsystem.api.annotation.constraint.SizeConstraint;
import org.genericsystem.api.core.Generic;

/**
 * <p>
 * An Attribute is a component of at least one <tt>Type</tt>.
 * </p>
 * <p>
 * An Attribute is a structural (model level).
 * </p>
 */
public interface Attribute extends Holder, Type {

	/**
	 * Disables property constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T disablePropertyConstraint();

	/**
	 * Disables required constraint
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint();

	/**
	 * Disables required constraint for the base position.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint(int componentPos);

	/**
	 * Disables singular constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingularConstraint();

	/**
	 * Disables singular constraint for the base position.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return this
	 */
	<T extends Type> T disableSingularConstraint(int componentPos);

	/**
	 * Disables Size Constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @param basePos
	 *            the component position restrained by the constraint
	 * 
	 * @return this
	 */
	<T extends Generic> T disableSizeConstraint(int basePos);

	/**
	 * Disables unique value constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T disableUniqueValueConstraint();

	/**
	 * Enables property constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T enablePropertyConstraint();

	/**
	 * Enables required constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T enableRequiredConstraint();

	/**
	 * Enables required constraint for the base position.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableRequiredConstraint(int componentPos);

	/**
	 * Enables singular constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint();

	/**
	 * Enables singular constraint for the base position
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(int componentPos);

	/**
	 * Sets a maximum number of <tt>Holder</tt> allowed.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @param basePos
	 *            The component position implicated by the constraint
	 * @param size
	 *            the maximum number of <tt>Holder</tt> allowed
	 * 
	 * @return this
	 * 
	 * @see SizeConstraint
	 */
	<T extends Generic> T enableSizeConstraint(int basePos, Integer size);

	/**
	 * Enables unique value constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return this
	 */
	<T extends Type> T enableUniqueValueConstraint();

}
