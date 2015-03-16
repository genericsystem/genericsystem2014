package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class LazyTest extends AbstractTest {

	// public void test_001() {
	// Root engine = new Root();
	// Generic vehicle = engine.addInstance("Vehicle");
	// Generic powerVehicle = vehicle.addAttribute("power");
	// Generic color = engine.addInstance("color");
	// LazyHandler lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "Car", Collections.emptyList());
	//
	// Generic myBmw = lazyCar.addInstance("myMbw");
	// Generic carColor = lazyCar.addRelation("carColor", color);
	//
	// assert powerVehicle == vehicle.getAttribute("power", vehicle);
	// }
	//
	// public void test_002() {
	// Root engine = new Root();
	// Generic vehicle = engine.addInstance("Vehicle");
	// Generic color = engine.addInstance("Color");
	// Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
	//
	// Generic car = engine.addInstance(vehicle, "Car");
	// Generic metalColor = engine.addInstance(color, "Metalcolor");
	// Generic carMetalcolor = car.addRelation(vehicleColor, "CarMetalcolor", metalColor);
	//
	// Generic blue = color.addInstance("blue");
	// Generic metalBlue = metalColor.addInstance("metalBlue");
	// Generic myBmw = car.addInstance("myMbw");
	//
	// System.out.println(car.getRelations().info());
	//
	// Generic myBmwblue = myBmw.addLink(vehicleColor, "myBmwblue", blue);// ???
	//
	// assert myBmw.addLink(vehicleColor, "myBmwmetalBlue", metalBlue).getMeta().equals(carMetalcolor);
	// System.out.println(myBmw.getLinks(vehicleColor).info());
	// System.out.println(myBmw.getLinks(carMetalcolor).info());
	//
	// }
	//
	// public void test_003() {
	// Root engine = new Root();
	// Generic vehicle = engine.addInstance("Vehicle");
	// Generic color = engine.addInstance("Color");
	// Generic blue = color.addInstance("blue");
	//
	// // Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
	// // LazyHandler myLazyCar = new LazyHandler(engine.getCurrentCache(), vehicle, Collections.emptyList(), "myLazyCar", Collections.emptyList());
	// // System.out.println(vehicle.getInstances().info());
	// // blue.addLink(vehicleColor, myLazyCar, "hjghj");
	// // System.out.println(vehicle.getInstances().info());
	//
	// }
}
