package org.genericsystem.api.model;

/**
 * <p>
 * A Relation connect <tt>Types</tt>.
 * </p>
 * <p>
 * A Relation is a structural (model level).
 * </p>
 * <p>
 * An instance of a Relation is a <tt>Link</tt>.
 * </p>
 *
 * @see Type
 * @see Link
 */
public interface Relation extends Attribute, Link {

	/**
	 * Disables cascade remove for the component position.
	 * 
	 * @param <T>
	 *            target of the disabling as a relation
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return this
	 */
	<T extends Relation> T disableCascadeRemove(int componentPos);

	/**
	 * Enables cascade remove for the component position.
	 * 
	 * @param <T>
	 *            target of the enabling as a relation
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Relation> T enableCascadeRemove(int componentPos);

	/**
	 * Returns {@code true} if the cascade remove enabled for the component position, {@code false} otherwise.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint
	 * 
	 * @return {@code true} if the cascade remove enabled for the component position, {@code false} otherwise
	 */
	boolean isCascadeRemove(int componentPos);

}
