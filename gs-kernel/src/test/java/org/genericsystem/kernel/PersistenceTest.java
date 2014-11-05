package org.genericsystem.kernel;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
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
		Vertex child = rootTree.setNode("Child");
		rootTree.setNode("Child2");
		child.setNode("Child3");
		root.close();
		compareGraph(root, new Root(Statics.ENGINE_VALUE, snapshot));
	}

	public void testInheritanceTree() {
		String snapshot = cleanDirectory(directoryPath + new Random().nextInt());
		Root root = new Root(Statics.ENGINE_VALUE, snapshot);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.setInheritingNode("Child");
		rootTree.setInheritingNode("Child2");
		child.setInheritingNode("Child3");
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

	private void compareGraph(Vertex persistedNode, Vertex readNode) {
		DependenciesOrder persistVisit = new DependenciesOrder().visit(persistedNode);
		DependenciesOrder readVisit = new DependenciesOrder().visit(readNode);
		assert persistVisit.size() == readVisit.size() : persistVisit + " \n " + readVisit;
		LOOP: for (Vertex persist : persistVisit) {
			for (Vertex read : readVisit)
				if (persist.equiv(read))
					continue LOOP;
			assert false : persistVisit + " \n " + readVisit;
		}
	}

	private class DependenciesOrder extends ArrayDeque<Vertex> {
		private static final long serialVersionUID = -5970021419012502402L;

		private DependenciesOrder visit(Vertex node) {
			if (!contains(node)) {
				node.getComposites().forEach(this::visit);
				node.getInheritings().forEach(this::visit);
				node.getInstances().forEach(this::visit);
				if (!node.isRoot())
					super.push(node);
			}
			return this;
		}
	}

}
