package org.genericsystem.api.model;

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
	 * Enable Size Constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * @param size
	 *            The size.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T enableSizeConstraint(int basePos, Integer size);

	/**
	 * Disable Size Constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T disableSizeConstraint(int basePos);

	/**
	 * Enable singular constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @return Return this.
	 */
	<T extends Type> T enableSingularConstraint();

	/**
	 * Disable singular constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint();

	/**
	 * Enable singular constraint for the base position
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return this
	 */
	<T extends Type> T enableSingularConstraint(int componentPos);

	/**
	 * Disable singular constraint for the base position.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint(int componentPos);

	/**
	 * Enable property constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return Return this
	 */
	<T extends Type> T enablePropertyConstraint();

	/**
	 * Disable property constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return Return this
	 */
	<T extends Type> T disablePropertyConstraint();

	/**
	 * Enable required constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint();

	/**
	 * Disable required constraint
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint();

	/**
	 * Enable required constraint for the base position.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint(int componentPos);

	/**
	 * Disable required constraint for the base position.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Type> T disableRequiredConstraint(int componentPos);

	/**
	 * Enable unique value constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @return Return this.
	 */
	<T extends Type> T enableUniqueValueConstraint();

	/**
	 * Disable unique value constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return Return this.
	 */
	<T extends Type> T disableUniqueValueConstraint();

}
