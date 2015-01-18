package org.genericsystem.kernel;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

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

	public void testHolder() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex vehiclePower = vehicle.setAttribute("power");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.setHolder(vehiclePower, "123");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testAddAndRemove() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(vehicle, "Car");
		root.addInstance(vehicle, "Truck");
		car.remove();
		root.close();
		Root root2 = new Root(Statics.ENGINE_VALUE, snapshot);
		compareGraph(root, root2);
	}

	public void testLink() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex color = root.addInstance("Color");
		Vertex vehicleColor = vehicle.setAttribute("VehicleColor", color);
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex red = color.addInstance("red");
		myVehicle.setHolder(vehicleColor, "myVehicleRed", red);
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testHeritageMultiple() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex robot = root.addInstance("Robot");
		root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testHeritageMultipleDiamond() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex nommable = root.addInstance("Nommable");
		Vertex vehicle = root.addInstance(nommable, "Vehicle");
		Vertex robot = root.addInstance(nommable, "Robot");
		root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testTree() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.setChild("Child");
		rootTree.setChild("Child2");
		child.setChild("Child3");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testInheritanceTree() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.setInheritingChild("Child");
		rootTree.setInheritingChild("Child2");
		child.setInheritingChild("Child3");
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

	private void compareGraph(Vertex persistedNode, Vertex readNode) {
		Collection<Vertex> persistVisit = persistedNode.getCurrentCache().computeDependencies(persistedNode);
		Collection<Vertex> readVisit = readNode.getCurrentCache().computeDependencies(readNode);
		assert persistVisit.size() == readVisit.size() : persistVisit + " \n " + readVisit;
		for (Vertex persist : persistVisit) {
			for (Vertex read : readVisit)
				if (persist == read)
					assert false : persistVisit + " \n " + readVisit;
		}
		LOOP: for (Vertex persist : persistVisit) {
			for (Vertex read : readVisit)
				if (persist.genericEquals(read))
					continue LOOP;
			assert false : persistVisit + " \n " + readVisit;
		}
	}

}
