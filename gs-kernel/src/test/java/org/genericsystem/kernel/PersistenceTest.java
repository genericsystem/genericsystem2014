package org.genericsystem.kernel;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import org.testng.annotations.Test;

@Test
public class PersistenceTest {

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
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
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
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
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
		Vertex child = rootTree.setSubNode("Child");
		rootTree.setSubNode("Child2");
		child.setSubNode("Child3");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	// public void testInheritanceTree() {
	// String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
	// Root root = new Root(Statics.ENGINE_VALUE, snapshot);
	// Vertex tree = root.addTree("Tree");
	// Vertex rootTree = tree.addRoot("Root");
	// Vertex child = rootTree.setInhertingSubNode("Child");
	// rootTree.setInhertingSubNode("Child2");
	// child.setInhertingSubNode("Child3");
	// root.close();
	// compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	// }

	private static String cleanDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (file.exists())
			for (File f : file.listFiles())
				f.delete();
		return directoryPath;
	}

	private void compareGraph(Vertex persistedNode, Vertex readNode) {
		readByInheritings(persistedNode, readNode);
		readByInstances(persistedNode, readNode);
		readByComposites(persistedNode, readNode);
	}

	private void readByInheritings(Vertex persistedNode, Vertex readNode) {
		assert persistedNode.getInheritings().size() == readNode.getInheritings().size() : persistedNode.getInheritings().info() + " / " + readNode.getInheritings().info();
		LOOP: for (Vertex persistedGeneric : persistedNode.getInheritings()) {
			for (Vertex read : readNode.getInheritings()) {
				if (persistedGeneric.equiv(read))
					continue LOOP;
			}
			assert false;
		}
	}

	private void readByInstances(Vertex persistedNode, Vertex readNode) {
		assert persistedNode.getInstances().size() == readNode.getInstances().size() : persistedNode.getInstances().info() + " / " + readNode.getInstances().info();
		LOOP: for (Vertex persistedGeneric : persistedNode.getInstances()) {
			for (Vertex read : readNode.getInstances()) {
				if (persistedGeneric.equiv(read))
					continue LOOP;
			}
			assert false;
		}
	}

	private void readByComposites(Vertex persistedNode, Vertex readNode) {
		assert persistedNode.getComposites().size() == readNode.getComposites().size() : persistedNode.getComposites().info() + " / " + readNode.getComposites().info();
		LOOP: for (Vertex persistedGeneric : persistedNode.getComposites()) {
			for (Vertex read : readNode.getComposites()) {
				if (persistedGeneric.equiv(read))
					continue LOOP;
			}
			assert false;
		}
	}

}