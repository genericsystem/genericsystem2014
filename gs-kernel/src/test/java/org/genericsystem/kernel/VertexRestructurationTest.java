package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

@Test
public class VertexRestructurationTest {

	public void test001_setValueOnInstance_easy_NewValue_OK() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String newValue = "elciheV";

		// when
		Vertex newVehicle = vehicle.setValue(newValue);

		// then
		assert newValue.equals(newVehicle.getValue());
	}

	public void test002_setValueOnInstance_easy_NewVertex_isAliveOK() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");

		// when
		Vertex newVehicle = vehicle.setValue("elciheV");

		// then
		assert newVehicle.isAlive();
	}

	public void test003_setValueOnInstance_easy_LastValue_OK() {
		// given
		Vertex engine = new Root();
		String lastValue = "Vehicle";
		Vertex vehicle = engine.addInstance(lastValue);
		String newValue = "elciheV";

		// when
		vehicle.setValue(newValue);

		// then
		assert lastValue.equals(vehicle.getValue());
	}

	public void test004_setValueOnInstance_easy_LastVertex_isAliveKO() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");

		// when
		vehicle.setValue("elciheV");

		// then
		assert !vehicle.isAlive();
	}

	public void test005_setValueOnInstance_easy_NewValue_differentLastValue() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");

		// when
		Vertex newVehicle = vehicle.setValue("elciheV");

		// then
		assert !vehicle.equals(newVehicle);
	}

	public void test006_setValueOnInstance_medium_LVL2_NewValueOnLVL1() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = vehicle.addInstance(valueCar);
		String newValue = "elciheV";

		// when
		Vertex newVehicle = vehicle.setValue(newValue);

		assert newValue.equals(newVehicle.getValue());
		assert valueCar.equals(car.getValue());
		assert engine == newVehicle.getMeta();
		assert engine.computeAllDependencies().contains(newVehicle);
		Vertex newCar = newVehicle.getInstances().iterator().next();
		assert newValue.equals(newCar.getMeta().getValue());
	}

	public void test006_setValueOnInstance_medium_LVL2_NewValueOnLVL1_NoCollateralDommage() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		vehicle.addInstance("Car");
		String caveValue = "Cave";
		Vertex cave = engine.addInstance(caveValue);

		// when
		vehicle.setValue("elciheV");

		assert caveValue.equals(cave.getValue());
		assert engine == cave.getMeta();
		assert cave.getInstances().size() == 0;
		assert cave.isAlive();
	}

	public void test007_setValueOnInstance_medium_LVL3_NewValueOnLVL1() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = vehicle.addInstance(valueCar);
		String valueNewBeetle = "NewBeetle";
		Vertex newBeetle = car.addInstance(valueNewBeetle);
		String newValue = "elciheV";

		// when
		Vertex newVehicle = vehicle.setValue(newValue);

		// then
		LinkedHashSet<Vertex> engineAliveDependencies = newVehicle.computeAllDependencies();
		assert engineAliveDependencies.size() == 3;
		assert !engineAliveDependencies.contains(vehicle);
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Vertex vertex1asNewVehicle = findElement(newValue, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex1asNewVehicle != null;
		assert engine.equals(vertex1asNewVehicle.getMeta());

		Vertex vertex2asNewCar = findElement(valueCar, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex2asNewCar != null;
		assert vertex1asNewVehicle.equals(vertex2asNewCar.getMeta());

		Vertex vertex3asNewNewBeetle = findElement(valueNewBeetle, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex3asNewNewBeetle != null;
		assert vertex2asNewCar.equals(vertex3asNewNewBeetle.getMeta());
	}

	public void test008_setValueOnInstance_medium_LVL3_NewValueOnLVL2() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = vehicle.addInstance(valueCar);
		String valueNewBeetle = "NewBeetle";
		Vertex newBeetle = car.addInstance(valueNewBeetle);
		String newValue = "raC";

		// when
		Vertex newCar = car.setValue(newValue);

		// then
		LinkedHashSet<Vertex> engineAliveDependencies = newCar.computeAllDependencies();
		assert engineAliveDependencies.size() == 2;
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Vertex vertex1asNewCar = findElement(newValue, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex1asNewCar != null;
		assert vehicle.equals(vertex1asNewCar.getMeta());

		Vertex vertex2asNewNewBeetle = findElement(valueNewBeetle, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex2asNewNewBeetle != null;
		assert vertex1asNewCar.equals(vertex2asNewNewBeetle.getMeta());
	}

	public void test020_setValueOnInheriting_medium_LVL2_NewValueOnLVL1() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = engine.addInstance(vehicle, valueCar);
		String newValue = "elciheV";

		// when
		Vertex newVehicle = vehicle.setValue(newValue);

		// then
		assert newVehicle.isAlive();
		assert !vehicle.isAlive();
		assert !car.isAlive();

		assert newValue.equals(newVehicle.getValue());
		assert engine.equals(newVehicle.getMeta());
		assert engine.computeAllDependencies().contains(newVehicle);
		assert newVehicle.computeAllDependencies().size() == 2;
		assert newVehicle.computeAllDependencies().contains(newVehicle);
		Vertex newCar = newVehicle.computeAllDependencies().stream().collect(Collectors.toList()).get(0);
		assert newCar.isAlive();
		if (newValue.equals(newCar.getValue()))
			newCar = newVehicle.computeAllDependencies().stream().collect(Collectors.toList()).get(1);
		assert engine.equals(newCar.getMeta());
		assert car.getValue().equals(newCar.getValue());
		List<Vertex> newCarSupers = newCar.getSupersStream().collect(Collectors.toList());
		assert newCarSupers.size() == 1;
		Vertex newVehicleFromNewCar = newCarSupers.get(0);
		assert newValue.equals(newVehicleFromNewCar.getValue());
	}

	public void test040_setValueOnComponent_medium_LVL2_NewValueOnLVL1() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valuePower = "Power";
		Vertex power = engine.addInstance(valuePower, vehicle);
		String newValue = "elciheV";

		// when
		Vertex newVehicle = vehicle.setValue(newValue);

		// then
		assert newValue.equals(newVehicle.getValue());
		assert !power.isAlive();
		assert engine.equals(newVehicle.getMeta());
		assert engine.computeAllDependencies().contains(newVehicle);
		Vertex newPower = findPower(newVehicle, valuePower);
		assert newPower.getComponentsStream().count() == 1;
		Vertex componentOfPower = newPower.getComponents().get(0);
		assert newVehicle.getValue().equals(componentOfPower.getValue());
		assert engine.equals(componentOfPower.getMeta());
	}

	private Vertex findPower(Vertex newVehicle, Serializable power) {
		for (Vertex vertex : newVehicle.computeAllDependencies())
			if (power.equals(vertex.getValue()))
				return vertex;
		return null;
	}

	public void test060_setValueOnAll_advanced_LVL3_NewValueOnLVL1() {
		// given
		Vertex engine = new Root();
		Vertex machine = engine.addInstance("Machine");
		Vertex vehicle = engine.addInstance(machine, "Vehicle");
		String valuePower = "Power";
		Vertex power = engine.addInstance(valuePower, vehicle);
		Vertex car = vehicle.addInstance("Car");
		String newValue = "enihcaM";

		// when
		Vertex newMachine = machine.setValue(newValue);

		// then 1. isAlive
		assert engine.isAlive();
		assert !machine.isAlive();
		assert !vehicle.isAlive();
		assert !power.isAlive();
		assert !car.isAlive();

		// then 2. metas
		assert engine.equals(engine.getMeta());
		assert engine.equals(machine.getMeta());
		assert engine.equals(vehicle.getMeta());
		assert engine.equals(power.getMeta());
		assert vehicle.equals(car.getMeta());

		// then 3. newValue of machine
		assert newValue.equals(newMachine.getValue());
		assert newMachine.getComponents().size() == 0;
		assert newMachine.getSupersStream().count() == 0;
		assert newMachine.computeAllDependencies().size() == 4;
		assert newMachine.getInstances().size() == 0;
		assert newMachine.getInheritings().size() == 1;

		// then 4. check if we do not lose a dependency or instance in the process via engine
		assert engine.getComponents().size() == 0;
		assert engine.getSupersStream().count() == 0;
		assert engine.computeAllDependencies().size() == 5;
		assert engine.getInstances().size() == 3;
		assert engine.getInheritings().size() == 0;

		// then 5. check newVehicle
		Vertex newVehicle = findElement("Vehicle", newMachine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newVehicle != null;
		assert newVehicle.getComponents().size() == 0;
		assert newVehicle.getSupersStream().count() == 1;
		assert newVehicle.computeAllDependencies().size() == 3;
		assert newVehicle.getInstances().size() == 1;
		assert newVehicle.getInheritings().size() == 0;

		// then 6. check newPower
		Vertex newPower = findElement("Power", newMachine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newPower != null;
		assert newPower.getComponents().size() == 1;
		assert newPower.getSupersStream().count() == 0;
		assert newPower.computeAllDependencies().size() == 1;
		assert newPower.getInstances().size() == 0;
		assert newPower.getInheritings().size() == 0;

		// then 7. check newCar
		Vertex newCar = findElement("Car", newMachine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newCar != null;
		assert newCar.getComponents().size() == 0;
		assert newCar.getSupersStream().count() == 0;
		assert newCar.computeAllDependencies().size() == 1;
		assert newCar.getInstances().size() == 0;
		assert newCar.getInheritings().size() == 0;
	}

	/**
	 * @param value
	 *            the value of the vertex expected
	 * @param vertexList
	 *            the list of vertex in which we find the value
	 * @return first vertex with the expected value, null otherwise
	 */
	private Vertex findElement(Serializable value, List<Vertex> vertexList) {
		assert value != null;
		for (Vertex element : vertexList)
			if (value.equals(element.getValue()))
				return element;
		return null;
	}

	public void test101_removeOnType_easy_OK() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");

		// when
		Vertex newVehicle = vehicle.remove();

		// then
		assert newVehicle == null;
		assert !vehicle.isAlive();
		assert engine.computeAllDependencies().stream().count() == 1;
		assert engine.computeAllDependencies().contains(engine);
	}

	public void test102_removeOnTypeWithSubType_medium_OK() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		printAll(vehicle);

		// when
		Vertex newVehicle = vehicle.remove();
		printAll(engine);

		// then
		assert newVehicle == null;
		assert !vehicle.isAlive();
		assert !car.isAlive();

		List<Vertex> engineDependencies = engine.computeAllDependencies().stream().collect(Collectors.toList());
		assert engineDependencies.size() == 2;
		Vertex newCar = findElement("Car", engineDependencies);
		printAll(newCar);
		assert newCar.isAlive();
		assert "Car".equals(newCar.getValue());
		assert newCar.computeAllDependencies().isEmpty();
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void printAll(Vertex vertex) {
		System.out.println("\\\\\\\\\\\\\\\\\\\\ Toutes les infos sur le vertex" + vertex.info() + "//////////");
		printComponents(vertex);
		printDependencies(vertex);
		printInheritings(vertex);
		printInstances(vertex);
		printSupers(vertex);
	}

	@Deprecated
	private void printInstances(Vertex vertex) {
		System.out.println("instances:-------------------" + vertex.info() + "---------------------");
		for (Vertex vertexInstance : vertex.getInstances())
			System.out.println(vertexInstance.info() + " - alive : " + vertexInstance.isAlive() + " - meta : " + vertexInstance.getMeta().info() + "meta alive : " + vertexInstance.getMeta().isAlive());
		System.out.println("----------------------------------------");
	}

	@Deprecated
	private void printDependencies(Vertex vertex) {
		System.out.println("dependencies:-------------------" + vertex.info() + "---------------------");
		for (Vertex vertexDependency : vertex.computeAllDependencies())
			System.out.println(vertexDependency.info() + " - alive : " + vertexDependency.isAlive() + " - meta : " + vertexDependency.getMeta().info() + "meta alive : " + vertexDependency.getMeta().isAlive());
		System.out.println("----------------------------------------");
	}

	@Deprecated
	private void printInheritings(Vertex vertex) {
		System.out.println("inheritings:-------------------" + vertex.info() + "---------------------");
		for (Vertex vertexInheriting : vertex.getInheritings())
			System.out.println(vertexInheriting.info() + " - alive : " + vertexInheriting.isAlive() + " - meta : " + vertexInheriting.getMeta().info() + "meta alive : " + vertexInheriting.getMeta().isAlive());
		System.out.println("----------------------------------------");
	}

	@Deprecated
	private void printSupers(Vertex vertex) {
		System.out.println("supers:-------------------" + vertex.info() + "---------------------");
		for (Vertex vertexSuper : vertex.getSupersStream().collect(Collectors.toList()))
			System.out.println(vertexSuper.info() + " - alive : " + vertexSuper.isAlive() + " - meta : " + vertexSuper.getMeta().info() + "meta alive : " + vertexSuper.getMeta().isAlive());
		System.out.println("----------------------------------------");
	}

	@Deprecated
	private void printComponents(Vertex vertex) {
		System.out.println("components:-------------------" + vertex.info() + "---------------------");
		for (Vertex vertexComponent : vertex.getComponents())
			System.out.println(vertexComponent.info() + " - alive : " + vertexComponent.isAlive() + " - meta : " + vertexComponent.getMeta().info() + "meta alive : " + vertexComponent.getMeta().isAlive());
		System.out.println("----------------------------------------");
	}

}
