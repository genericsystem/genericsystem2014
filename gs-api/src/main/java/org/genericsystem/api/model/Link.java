package org.genericsystem.api.model;

import org.genericsystem.api.core.Generic;

/**
 * Instance of a <tt>Relation</tt>
 * 
 * @see Relation
 */
public interface Link extends Holder {

	/**
	 * Returns the target component.
	 * 
	 * @param <T>
	 *            component as generic
	 * 
	 * @return Returns the target component
	 */
	<T extends Generic> T getTargetComponent();

}
