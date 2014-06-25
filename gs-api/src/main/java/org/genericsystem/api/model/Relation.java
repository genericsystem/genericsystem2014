package org.genericsystem.api.model;

/**
 * A Relation. Connect types.
 */
public interface Relation extends Attribute, Link {

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
	 * Returns true if the cascade remove enabled for the component position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the cascade remove enabled for the component position
	 */
	boolean isCascadeRemove(int componentPos);

}
