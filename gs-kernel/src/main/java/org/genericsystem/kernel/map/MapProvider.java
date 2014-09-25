package org.genericsystem.kernel.map;

import java.io.Serializable;
import java.util.Map;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.IRoot;
import org.genericsystem.kernel.IVertex;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface MapProvider<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertex<T, U> {

	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getExtendedMap(T t);

}
