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
	 * Disable property constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return Return this
	 */
	<T extends Type> T disablePropertyConstraint();

	/**
	 * Disable required constraint
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return this
	 */
	<T extends Type> T disableRequiredConstraint();

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
	 * Disable singular constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return Return this
	 */
	<T extends Type> T disableSingularConstraint();

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
	 * Disable unique value constraint.
	 * 
	 * @param <T>
	 *            target of the disabling as a type
	 * @return Return this.
	 */
	<T extends Type> T disableUniqueValueConstraint();

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
	 * Enable required constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * 
	 * @return Return this.
	 */
	<T extends Type> T enableRequiredConstraint();

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
	 * Enable singular constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @return Return this.
	 */
	<T extends Type> T enableSingularConstraint();

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
	 * Enable unique value constraint.
	 * 
	 * @param <T>
	 *            target of the enabling as a type
	 * @return Return this.
	 */
	<T extends Type> T enableUniqueValueConstraint();

}
