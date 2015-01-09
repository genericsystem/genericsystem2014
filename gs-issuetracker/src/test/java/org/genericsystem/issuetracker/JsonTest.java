package org.genericsystem.issuetracker;

import java.io.ByteArrayInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

@Test
public class JsonTest {
	protected static Logger log = LoggerFactory.getLogger(JsonTest.class);

	public void createAndParseJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("Key", "Value");
		String jsonFlux = builder.build().toString();
		log.info(jsonFlux);

		JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonFlux.getBytes()));
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		for (String key : jsonObject.keySet())
			log.info(key + " " + jsonObject.get(key).toString());
	}
}
