package org.genericsystem.api.model;

import org.genericsystem.api.core.Generic;

/**
 * A Link <br>
 * Link any the instances of the Types.
 */
public interface Link extends Holder {

	/**
	 * Returns the target component.
	 * 
	 * @param <T>
	 *            component as generic.
	 * @return Returns the target component.
	 */
	<T extends Generic> T getTargetComponent();

}
