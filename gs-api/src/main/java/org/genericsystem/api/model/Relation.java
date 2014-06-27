package org.genericsystem.api.model;

/**
 * <p>
 * A Relation connect <tt>Type</tt>s.
 * </p>
 * <p>
 * A Relation is a structural (model level).
 * </p>
 * <p>
 * An instance of a Relation is a <tt>Link</tt>
 * </p>
 *
 * @see Type
 * @see Link
 */
public interface Relation extends Attribute, Link {

	/**
	 * Disable cascade remove for the component position
	 * 
	 * @param <T>
	 *            target of the disabling as a relation
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Relation> T disableCascadeRemove(int componentPos);

	/**
	 * Enable cascade remove for the component position.
	 * 
	 * @param <T>
	 *            target of the enabling as a relation
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Relation> T enableCascadeRemove(int componentPos);

	/**
	 * Returns true if the cascade remove enabled for the component position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the cascade remove enabled for the component position
	 */
	boolean isCascadeRemove(int componentPos);

}
