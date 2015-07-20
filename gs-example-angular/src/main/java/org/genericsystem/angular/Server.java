package org.genericsystem.angular;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.example.util.ExampleRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.angular.annotation.Column;
import org.genericsystem.angular.annotation.Table;
import org.genericsystem.angular.model.Capacity;
import org.genericsystem.angular.model.Car;
import org.genericsystem.angular.model.Color;
import org.genericsystem.angular.model.Options;
import org.genericsystem.angular.model.Power;
import org.genericsystem.angular.model.Shade;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Cache;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Server extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(Server.class);

	private Engine engine = new Engine(Car.class, Power.class, Color.class, Options.class, Shade.class, Capacity.class);
	private Map<String, Class<?>> classes = new LinkedHashMap<String, Class<?>>() {
		private static final long serialVersionUID = 5249422060289303031L;
		{
			put("color", Color.class);
			put("options", Options.class);
			put("car", Car.class);
		}
	};

	public String getTableName(Generic type) {
		if (!type.isSystem())
			return null;
		Class<?> clazz = engine.findAnnotedClass(type);
		Table annotation = clazz.getAnnotation(Table.class);
		return annotation != null ? annotation.value() : null;
	}

	public String getColumnName(Generic attribute) {
		if (!attribute.isSystem())
			return null;
		Class<?> clazz = engine.findAnnotedClass(attribute);
		Column annotation = clazz.getAnnotation(Column.class);
		return annotation != null ? annotation.value() : null;
	}

	public JsonArray getGSJsonCRUD() {
		JsonArray jsonArray = new JsonArray();
		for (Generic type : engine.getSubInstances().filter(typ -> typ.getComponents().isEmpty())) {
			String tableName = getTableName(type);
			JsonObject json = new JsonObject();
			if (tableName != null)
				json.put("tableName", tableName);
			JsonArray attributesArray = new JsonArray();
			for (Generic attribute : type.getAttributes().filter(att -> att.getComponents().size() == 1 && type.inheritsFrom(att.getBaseComponent()))) {
				String columnName = getColumnName(attribute);
				if (columnName != null)
					attributesArray.add(new JsonObject().put("columnName", columnName));
			}
			json.put("columns", attributesArray);
			jsonArray.add(json);
		}
		return jsonArray;
	}

	public Snapshot<Generic> getTypeAttributes(Generic type) {
		return type.getAttributes().filter(att -> att.getComponents().size() == 1 && type.inheritsFrom(att.getBaseComponent()));
	}

	public Generic getInstanceById(Generic type, Long id) {
		return type.getInstances().stream().filter(g -> Objects.equals((id), g.getTs())).findFirst().orElse(null);
	}

	public Serializable convert(Generic attribute, String value) {
		Class<? extends Serializable> classConstraint = attribute.getInstanceValueClassConstraint();
		if (classConstraint.equals(java.lang.Integer.class))
			return Integer.parseInt(value);
		return null;
	}

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		ExampleRunner.runJavaExample("src/main/java/", Server.class, false);
	}

	private JsonObject getJson(Generic instance, Snapshot<Generic> attributes) {
		final JsonObject json = new JsonObject();
		json.put("id", String.valueOf(instance.getTs()));
		json.put("value", instance.getValue().toString());
		for (Generic attribute : attributes)
			json.put(getColumnName(attribute), Objects.toString(instance.getValue(attribute)));
		return json;
	}

	JsonArray jsonArray = getGSJsonCRUD();

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.route().handler(CookieHandler.create());
		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
		router.route().handler(ctx -> {
			Session session = ctx.session();
			Cache cache = session.get("cache");
			if (cache == null)
				session.put("cache", cache = engine.newCache());
			cache.start();
			ctx.next();
		});

		router.get("/api/types").handler(ctx -> {
			ctx.response().end(jsonArray.encode());
		});

		for (int j = 0; j <= jsonArray.size() - 1; j++) {
			String typeName = jsonArray.getJsonObject(j).getString("tableName");
			router.get("/api/" + typeName).handler(ctx -> {
				log.info("launch handler for type : " + typeName);
				Generic type = engine.getInstance(typeName);
				final JsonArray json = new JsonArray();
				type.getInstances().stream().forEach(i -> json.add(getJson(i, getTypeAttributes(type))));
				ctx.response().end(json.encode());
			});
			router.get("/api/" + typeName + "/:id").handler(ctx -> {
				Generic type = engine.getInstance(typeName);
				Generic instance = getInstanceById(type, Long.valueOf(ctx.request().getParam("id")));
				JsonObject json = getJson(instance, getTypeAttributes(type));
				ctx.response().end(json.encode());
			});
			router.post("/api/" + typeName).handler(ctx -> {
				Generic type = engine.getInstance(typeName);
				JsonObject newInst = ctx.getBodyAsJson();
				Generic instance = type.setInstance(newInst.getString("value"));
				for (Generic attribute : getTypeAttributes(type))
					instance.setHolder(attribute, convert(attribute, newInst.getString(getColumnName(attribute))));
				ctx.response().end(newInst.encode());
			});
			router.put("/api/" + typeName + "/:id").handler(ctx -> {
				Generic type = engine.getInstance(typeName);
				Generic instance = getInstanceById(type, Long.valueOf(ctx.request().getParam("id")));
				JsonObject update = ctx.getBodyAsJson();
				instance.updateValue(update.getString("value"));
				for (Generic attribute : getTypeAttributes(type))
					instance.getHolder(attribute).updateValue(convert(attribute, update.getString(getColumnName(attribute))));
				JsonObject json = getJson(instance, getTypeAttributes(type));
				ctx.response().end(json.encode());
			});
			router.delete("/api/" + typeName + "/:id").handler(ctx -> {
				Generic type = engine.getInstance(typeName);
				Generic instance = getInstanceById(type, Long.valueOf(ctx.request().getParam("id")));

				instance.remove();
				ctx.response().setStatusCode(204);
				ctx.response().end();
			});
			router.put("/api/" + typeName).handler(ctx -> {
				engine.getCurrentCache().flush();
				ctx.response().end();
			});

			router.delete("/api/" + typeName + "/shift").handler(ctx -> {
				engine.getCurrentCache().shiftTs();
				ctx.response().end();
			});

			router.delete("/api/" + typeName + "/clear").handler(ctx -> {
				engine.getCurrentCache().clear();
				ctx.response().end();
			});

		}
		// Create a router endpoint for the static content.
		router.route().handler(StaticHandler.create());
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}
}
