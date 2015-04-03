package org.genericsystem.exampleswing.business;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CarBusiness {

	private Engine engine;

	private CarBusiness() {
		engine = new Engine(Car.class, Power.class);
	}

	public static CarBusiness getInstance() {
		return Singleton.carBusinessInstance;
	}

	private static class Singleton {
		private static CarBusiness carBusinessInstance = new CarBusiness();
	}

	public Generic getGeneric() {
		return engine.find(Car.class);
	}

	public Snapshot<Generic> getAllInstances() {
		return getGeneric().getSubInstances();
	}

	public void addCar(String value) {
		Generic car = engine.find(Car.class);
		Generic newCar = car.addInstance(value);
		System.out.println(newCar.info());
	}

	public void flush() {
		engine.getCurrentCache().flush();
		for (Generic generic : getAllInstances())
			System.out.println("Car : " + generic.getValue());
	}

	public void cancel() {
		engine.getCurrentCache().clear();
	}

}
