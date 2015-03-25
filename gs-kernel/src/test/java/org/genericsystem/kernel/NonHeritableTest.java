package org.genericsystem.kernel;

import java.util.Objects;

import org.testng.annotations.Test;

@Test
public class NonHeritableTest extends AbstractTest {

	public void test_enable() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		tree.enableHeritable();
		assert tree.isHeritableEnabled();
	}

	public void test_enableByDefault() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		assert tree.isHeritableEnabled();
	}

	public void test_disable() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		tree.disableHeritable();
		assert !tree.isHeritableEnabled();
	}

	public void test_disable_then_enable() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		assert tree.isHeritableEnabled();
		tree.disableHeritable();
		assert !tree.isHeritableEnabled();
		tree.enableHeritable();
		assert tree.isHeritableEnabled();
		tree.disableHeritable();
	}

	public void test_enable_then_disable() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		tree.enableHeritable();
		assert tree.isHeritableEnabled();
		tree.disableHeritable();
		assert !tree.isHeritableEnabled();
	}

	public void test_default_attribute() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic defaultPower = car.addHolder(power, 233);
		Generic myCar = car.addInstance("myCar");
		power.disableHeritable();
		assert myCar.getHolder(power) == null;
		power.enableHeritable();
		assert myCar.getHolder(power).equals(defaultPower);
	}

	public void test_attribute() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic defaultPower = vehicle.addHolder(power, 233);
		assert defaultPower.equals(car.getHolder(power));
		power.disableHeritable();
		assert car.getHolder(power) == null;
		Generic defaultCarPower = car.addHolder(power, defaultPower, 256);
		Generic myCar = car.addInstance("myBmw");
		assert myCar.getHolder(power) == null;
		power.enableHeritable();
		assert defaultCarPower.equals(myCar.getHolder(power)) : myCar.getHolder(power);
	}

	public void test_tree_color() {
		Root engine = new Root();

		Generic tree = engine.addInstance("Tree");
		Generic color = engine.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);

		treeColor.disableHeritable();

		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Generic html = tree.addInstance("html");
		Generic head = tree.addInstance(html, "head");
		Generic body = tree.addInstance(html, "body");
		Generic div = tree.addInstance(body, "div");

		html.setHolder(treeColor, "htmlIsRed", red);
		div.setHolder(treeColor, "divIsGreen", green);

		assert Objects.equals(tree.getHolders(treeColor).first().getTargetComponent(), blue);
		assert Objects.equals(head.getHolders(treeColor).first(), null);
		assert Objects.equals(body.getHolders(treeColor).first(), null);
		assert Objects.equals(div.getHolders(treeColor).first().getTargetComponent(), green);
	}

	public void test_tree_color_disable_enable() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		Generic color = engine.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);
		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");
		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);
		Generic html = tree.addInstance("html");
		Generic head = tree.addInstance(html, "head");
		Generic body = tree.addInstance(html, "body");
		Generic div = tree.addInstance(body, "div");

		html.setHolder(treeColor, "htmlIsRed", red);
		div.setHolder(treeColor, "divIsGreen", green);

		treeColor.disableHeritable();
		assert Objects.equals(tree.getHolders(treeColor).first().getTargetComponent(), blue);
		assert Objects.equals(head.getHolders(treeColor).first(), null);
		assert Objects.equals(body.getHolders(treeColor).first(), null);
		assert Objects.equals(div.getHolders(treeColor).first().getTargetComponent(), green);

		treeColor.enableHeritable();
		assert Objects.equals(tree.getHolders(treeColor).first().getTargetComponent(), blue);
		assert Objects.equals(head.getHolders(treeColor).first().getTargetComponent(), red);
		assert Objects.equals(body.getHolders(treeColor).first().getTargetComponent(), red);
		assert Objects.equals(div.getHolders(treeColor).first().getTargetComponent(), green);
	}

}
