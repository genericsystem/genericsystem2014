package org.genericsystem.api.map;

import java.io.Serializable;
import java.util.Map;

import org.genericsystem.api.core.Generic;
import org.genericsystem.api.model.Attribute;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface MapProvider extends Attribute {

	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getExtendedMap(Generic generic);

}
