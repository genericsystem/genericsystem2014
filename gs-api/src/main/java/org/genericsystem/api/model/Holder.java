package org.genericsystem.api.model;

import org.genericsystem.api.core.Generic;

/**
 * <p>
 * The Holder of a data. Instantiating a Holder enables to save data, it is either an instance either a link.
 * </p>
 */
public interface Holder extends Generic {

	/**
	 * Returns the base component.
	 * 
	 * @param <T>
	 *            the base component as a generic
	 * 
	 * @return Returns the base component
	 */
	// TODO what should be done if several in different (int) basePos ?
	<T extends Generic> T getBaseComponent();

	/**
	 * Returns the component for the position specified.
	 * 
	 * @param <T>
	 *            the component as a generic
	 * @param basePos
	 *            the base position of the component searched
	 * 
	 * @return Returns the component if it exist else null
	 */
	<T extends Generic> T getComponent(int basePos);

	/**
	 * Returns the size implicated by the constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint
	 * 
	 * @return Return the size
	 */
	Integer getSizeConstraint(int basePos);

	/**
	 * Returns {@code true} if the property constraint enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if the property constraint enabled, {@code false} otherwise
	 */
	boolean isPropertyConstraintEnabled();

	/**
	 * Returns {@code true} if the required constraint enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if the required constraint enabled, {@code false} otherwise
	 */
	boolean isRequiredConstraintEnabled();

	/**
	 * Returns {@code true} if the required constraint enabled for the base position, {@code false} otherwise.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * @return {@code true} if the required constraint enabled for the base position, {@code false} otherwise
	 */
	boolean isRequiredConstraintEnabled(int componentPos);

	/**
	 * Returns {@code true} if the singular constraint enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if the singular constraint enabled, {@code false} otherwise
	 */
	boolean isSingularConstraintEnabled();

	/**
	 * Returns {@code true} if the singular constraint enabled for the base position, {@code false} otherwise.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return {@code true} if the singular constraint enabled for the base position, {@code false} otherwise
	 */
	boolean isSingularConstraintEnabled(int componentPos);

	/**
	 * Returns {@code true} if the unique value constraint enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if the unique value constraint enabled, {@code false} otherwise
	 */
	boolean isUniqueValueConstraintEnabled();

}
