package org.genericsystem.kernel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.kernel.Archiver.AbstractWriterLoader.DependenciesOrder;
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

		// Root root2 = new Root(Statics.ENGINE_VALUE, snapshot);
		// root2.close();
		// Root root3 = new Root(Statics.ENGINE_VALUE, snapshot);
		// root3.close();
		// compareOrderGraph(root2, root3);
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

	private void compareOrderGraph(Vertex persistedNode, Vertex readNode) {
		DependenciesOrder persistVisit = new DependenciesOrder().visit(persistedNode);
		DependenciesOrder readVisit = new DependenciesOrder().visit(readNode);
		assert persistVisit.size() == readVisit.size() : persistVisit + " \n " + readVisit;
		for (Vertex persist : persistVisit) {
			for (Vertex read : readVisit)
				if (persist == read)
					assert false : persistVisit + " \n " + readVisit;
		}
		ArrayDeque<Vertex> clone = readVisit.clone();
		for (Vertex persist : persistVisit) {
			Vertex read = readVisit.pop();
			assert equals(persist, read) : persistVisit + " \n " + clone;
		}
	}

	private void compareGraph(Vertex persistedNode, Vertex readNode) {
		DependenciesOrder persistVisit = new DependenciesOrder().visit(persistedNode);
		DependenciesOrder readVisit = new DependenciesOrder().visit(readNode);
		assert persistVisit.size() == readVisit.size() : persistVisit + " \n " + readVisit;
		for (Vertex persist : persistVisit) {
			for (Vertex read : readVisit)
				if (persist == read)
					assert false : persistVisit + " \n " + readVisit;
		}
		LOOP: for (Vertex persist : persistVisit) {
			for (Vertex read : readVisit)
				if (equals(persist, read))
					continue LOOP;
			assert false : persistVisit + " \n " + readVisit;
		}
	}

	private static boolean equals(IVertex<?> node1, IVertex<?> node2) {
		boolean equals = equals(node1, node2.getMeta(), node2.getSupers(), node2.getValue(), node2.getComponents());
		System.out.println(node1.info() + " / " + node2.info() + " " + equals);
		return equals;
	}

	private static boolean equals(IVertex<?> node1, IVertex<?> meta, List<? extends IVertex<?>> supers, Serializable value, List<? extends IVertex<?>> components) {
		List<IVertex<?>> componentsList = (List<IVertex<?>>) node1.getComponents();
		for (int i = 0; i < componentsList.size(); i++) {
			if (!node1.equals(componentsList.get(i)) && components.get(i) != null && !equals(componentsList.get(i), components.get(i)))
				return false;
		}
		return (node1.isRoot() || node1.getMeta().equals(meta)) && Objects.equals(node1.getValue(), value)
		/* && node1.getComponents().equals(components.stream().map(x -> x == null ? node1 : x).collect(Collectors.toList())) */
		&& node1.getSupers().equals(supers);
	}

}
