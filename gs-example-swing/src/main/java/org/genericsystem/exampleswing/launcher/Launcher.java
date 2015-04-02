package org.genericsystem.exampleswing.launcher;

import javax.swing.SwingUtilities;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.exampleswing.swingcomponents.SnapshotViewer;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Launcher {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new SnapshotViewer(getAllInstancesSnapshot());
			}
		});

	}

	public static Snapshot<Generic> getAllInstancesSnapshot() {
		Engine engine = new Engine(Car.class, Power.class);
		engine.newCache().start();
		Generic car = engine.find(Car.class);
		Generic power = engine.find(Power.class);
		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		car.addInstance("myMercedes");
		car.addInstance("myPorsche");
		car.addInstance("myNewBeetle");

		Generic carPower = car.addAttribute(power, "Power");
		car.addHolder(carPower, 233);

		myBmw.addHolder(carPower, 123);
		myAudi.addHolder(carPower, 125);

		engine.getCurrentCache().flush();
		return car.getSubInstances();
	}
}
