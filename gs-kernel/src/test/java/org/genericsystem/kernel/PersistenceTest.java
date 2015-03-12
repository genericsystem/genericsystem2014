package org.genericsystem.kernel;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.genericsystem.defaults.DefaultLifeManager;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.testng.annotations.Test;

@Test
public class PersistenceTest extends AbstractTest {

	private final String directoryPath = System.getenv("HOME") + "/test/snapshot_save";

	public void testDefaultConfiguration() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testType() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		root.addInstance("Vehicle");
		root.close();
		Root engine = new Root(Statics.ENGINE_VALUE, snapshot);
		compareGraph(root, engine);
		assert null != engine.getInstance("Vehicle");
	}

	public void testTypeAnnoted() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root engine = new Root(Statics.ENGINE_VALUE, snapshot, Vehicle.class);
		Generic vehicle = engine.find(Vehicle.class);
		vehicle.addInstance("myVehicle");
		assert vehicle.getLifeManager().getBirthTs() == DefaultLifeManager.TS_SYSTEM;
		assert vehicle.getInstance("myVehicle").getLifeManager().getBirthTs() > vehicle.getLifeManager().getBirthTs();
		assert vehicle.isSystem();
		engine.close();

		Root engine2 = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic vehicle2 = engine2.getInstance(Vehicle.class);
		assert vehicle2.getInstance("myVehicle").getLifeManager().getBirthTs() > vehicle2.getLifeManager().getBirthTs();
		assert !vehicle2.isSystem();
		engine2.close();

		Root engine3 = new Root(Statics.ENGINE_VALUE, snapshot, Vehicle.class);
		Generic vehicle3 = engine3.find(Vehicle.class);
		assert vehicle3.getLifeManager().getBirthTs() == DefaultLifeManager.TS_SYSTEM;
		assert vehicle3.getInstance("myVehicle").getLifeManager().getBirthTs() > vehicle3.getLifeManager().getBirthTs();
		assert vehicle3.isSystem();
		engine3.close();
	}

	@SystemGeneric
	public static class Vehicle {

	}

	public void testHolder() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic vehicle = root.addInstance("Vehicle");
		Generic vehiclePower = vehicle.setAttribute("power");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setHolder(vehiclePower, "123");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testAddAndRemove() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		root.addInstance(vehicle, "Truck");
		car.remove();
		root.close();
		Root root2 = new Root(Statics.ENGINE_VALUE, snapshot);
		compareGraph(root, root2);
	}

	public void testLink() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.setAttribute("VehicleColor", color);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		myVehicle.setHolder(vehicleColor, "myVehicleRed", red);
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testHeritageMultiple() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic vehicle = root.addInstance("Vehicle");
		Generic robot = root.addInstance("Robot");
		root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testHeritageMultipleDiamond() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic nommable = root.addInstance("Nommable");
		Generic vehicle = root.addInstance(nommable, "Vehicle");
		Generic robot = root.addInstance(nommable, "Robot");
		root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testTree() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Generic tree = root.addInstance("Tree");
		Generic rootTree = tree.addInstance("Root");
		Generic child = tree.addInstance(rootTree, "Child");
		tree.addInstance(rootTree, "Child2");
		tree.addInstance(child, "Child3");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	private static String cleanDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (file.exists())
			for (File f : file.listFiles())
				f.delete();
		return directoryPath;
	}

	// private void compareOrderGraph(Vertex persistedNode, Vertex readNode) {
	// DependenciesOrder<Vertex> persistVisit = new DependenciesOrder<Vertex>().visit(persistedNode);
	// DependenciesOrder<Vertex> readVisit = new DependenciesOrder<Vertex>().visit(readNode);
	// assert persistVisit.size() == readVisit.size() : persistVisit + " \n " + readVisit;
	// for (Vertex persist : persistVisit) {
	// for (Vertex read : readVisit)
	// if (persist == read)
	// assert false : persistVisit + " \n " + readVisit;
	// }
	// ArrayDeque<Vertex> clone = readVisit.clone();
	// for (Vertex persist : persistVisit) {
	// Vertex read = readVisit.pop();
	// assert persist.genericEquals(read) : persistVisit + " \n " + clone;
	// }
	// }

	private void compareGraph(Generic persistedNode, Generic readNode) {
		Collection<Generic> persistVisit = persistedNode.getCurrentCache().computeDependencies(persistedNode);
		Collection<Generic> readVisit = readNode.getCurrentCache().computeDependencies(readNode);
		assert persistVisit.size() == readVisit.size() : persistVisit + " \n " + readVisit;
		for (Generic persist : persistVisit) {
			for (Generic read : readVisit)
				if (persist == read)
					assert false : persistVisit + " \n " + readVisit;
		}
		LOOP: for (Generic persist : persistVisit) {
			for (Generic read : readVisit)
				if (persist.genericEquals(read))
					continue LOOP;
			assert false : persistVisit + " \n " + readVisit;
		}
	}

}
