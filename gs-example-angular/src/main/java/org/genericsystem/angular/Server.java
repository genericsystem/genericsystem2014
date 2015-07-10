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

import java.util.Objects;

import model.Car;
import model.Power;

import org.genericsystem.mutability.Cache;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Server extends AbstractVerticle {

	private static final Logger log = LoggerFactory.getLogger(Server.class);

	Engine engine = new Engine(Car.class, Power.class);

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		ExampleRunner.runJavaExample("src/main/java/", Server.class, false);
	}

	public JsonObject getJson(Generic instance, Generic... attributes) {

		final JsonObject json = new JsonObject();
		json.put("id", String.valueOf(instance.getTs()));
		json.put(instance.getMeta().getValue().toString(), instance.getValue().toString());
		for (Generic attribute : attributes)
			json.put(attribute.getValue().toString(), instance.getHolder(attribute).getValue().toString());
		return json;
	}

	public JsonObject updateNode(Generic instance, JsonObject update, Generic... attributes) {
		instance.updateValue(update.getString(instance.getMeta().getValue().toString()));
		for (Generic attribute : attributes)
			instance.getHolder(attribute).updateValue(Integer.valueOf(update.getString(attribute.getValue().toString())));
		JsonObject json = getJson(instance, attributes);
		return json;

	}

	@Override
	public void start() throws Exception {

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.route().handler(CookieHandler.create());
		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
		router.route().handler(ctx -> {
			Session session = ctx.session();
			Cache cache = session.get("cache");
			if (cache == null) {

				session.put("cache", cache = engine.newCache());
			}
			cache.start();
			ctx.next();

		});
		// define some REST API
		router.get("/api/cars").handler(ctx -> {
			Generic car = engine.find(Car.class);
			Generic power = engine.find(Power.class);

			// now convert the list to a JsonArray because it will be easier to encode the final object as the response.

				final JsonArray json = new JsonArray();

				car.getInstances().stream().forEach(i -> json.add(getJson(i, power)));

				ctx.response().putHeader("cars", "application/json");
				ctx.response().end(json.encode());
			});

		router.get("/api/cars/:id").handler(ctx -> {
			Generic car = engine.find(Car.class);
			Generic power = engine.find(Power.class);

			Generic instance = car.getInstances().stream().filter(g -> Objects.equals(Long.valueOf(ctx.request().getParam("id")), g.getTs())).findFirst().orElse(null);
			JsonObject json = getJson(instance, power);
			ctx.response().putHeader("car", "application/json");
			ctx.response().end(json.encode());
		});

		router.post("/api/cars").handler(ctx -> {
			Generic car = engine.find(Car.class);
			Generic power = engine.find(Power.class);

			JsonObject newCar = ctx.getBodyAsJson();

			car.setInstance(newCar.getString("Car")).setHolder(power, Integer.parseInt(newCar.getString("Power")));

			ctx.response().putHeader("car", "application/json");
			ctx.response().end(newCar.encode());

		});

		router.put("/api/cars/:id").handler(ctx -> {
			Generic car = engine.find(Car.class);
			Generic power = engine.find(Power.class);

			Generic instance = car.getInstances().stream().filter(g -> Objects.equals(Long.valueOf(ctx.request().getParam("id")), g.getTs())).findFirst().orElse(null);
			JsonObject update = ctx.getBodyAsJson();
			JsonObject json = updateNode(instance, update, power);

			ctx.response().putHeader("car", "application/json");
			ctx.response().end(json.encode());

		});

		router.delete("/api/cars/:id").handler(ctx -> {
			Generic car = engine.find(Car.class);
			Generic power = engine.find(Power.class);

			Generic instance = car.getInstances().stream().filter(g -> Objects.equals(Long.valueOf(ctx.request().getParam("id")), g.getTs())).findFirst().orElse(null);
			instance.remove();
			ctx.response().setStatusCode(204);
			ctx.response().end();
		});

		// Implémentation du flush/clear/shift GS

		router.put("/api/cars").handler(ctx -> {
			engine.getCurrentCache().flush();
			ctx.response().end();
		});

		router.delete("/api/cars/shift").handler(ctx -> {
			engine.getCurrentCache().shiftTs();
			ctx.response().end();
		});

		router.delete("/api/cars/clear").handler(ctx -> {
			engine.getCurrentCache().clear();
			ctx.response().end();
		});

		// Create a router endpoint for the static content.
		router.route().handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

}
