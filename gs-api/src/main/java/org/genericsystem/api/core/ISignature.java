package org.genericsystem.api.core;

import java.io.Serializable;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Nicolas Feybesse
 *
 * @param <T>
 *            the implementation of this interface
 */
public interface ISignature<T extends ISignature<T>> {

	/**
	 * Returns the meta of this signature
	 *
	 * @return the Signature for which this signature is an instance. <br>
	 *         This method returns this for the root signature
	 */
	T getMeta();

	/**
	 * Returns the supers of this signature
	 *
	 * @return the list of supers signatures<br>
	 *         This list can be empty
	 *
	 */
	List<T> getSupers();

	/**
	 * Returns the value of this signature
	 *
	 * @return Returns the serializable value of this signature<br>
	 *         This value can be null
	 *
	 */
	Serializable getValue();

	/**
	 * Returns the components of this signature
	 *
	 * @return Returns the value of this signature<br>
	 *         This value can be null
	 *
	 */
	List<T> getComponents();

	/**
	 * Returns the JSonId of this signature
	 *
	 * @return Returns the JSonObject representing this signature
	 *
	 */
	default JsonObject toJSonId() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("Id", System.identityHashCode(this));
		builder.add("Value", toString());
		builder.add("Meta", System.identityHashCode(getMeta()));
		JsonArrayBuilder supersBuilder = Json.createArrayBuilder();
		for (T superVertex : getSupers())
			supersBuilder.add(System.identityHashCode(superVertex));
		builder.add("Supers", supersBuilder);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (T component : getComponents())
			arrayBuilder.add(System.identityHashCode(component));
		builder.add("Components", arrayBuilder);
		return builder.build();
	}

}
