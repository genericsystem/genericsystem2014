package org.genericsystem.admin;

import javax.swing.SwingUtilities;

import org.genericsystem.mutability.Engine;

public class Launcher {

	public static void main(String[] args) {
		Engine engine = new Engine(Car.class, Power.class);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// new InstancesEditor(engine.find(Car.class));
				new InstanceEditor(engine.find(Car.class).setInstance("myCar"));
			}
		});
	}
}
