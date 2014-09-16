package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public interface ISignature<T extends ISignature<T>> {

	T getMeta();

	List<T> getSupers();

	Serializable getValue();

	List<T> getComponents();

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
