package org.genericsystem.api.map;

import java.io.Serializable;
import java.util.Map;

import org.genericsystem.api.core.Generic;
import org.genericsystem.api.model.Attribute;

/**
 * 
 */
public interface MapProvider extends Attribute {

	/**
	 * Returns the map of generics associated with the generic specified. Returns an empty map if none is found.
	 * 
	 * @param <Key>
	 *            key as a Serializable
	 * @param <Value>
	 *            value linked to the key as a Serializable
	 * @param generic
	 *            the generic targeted
	 * 
	 * @return the map of generics associated with the generic specified. Returns an empty map if none is found
	 */
	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getExtendedMap(Generic generic);

}
