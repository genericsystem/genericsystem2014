package org.genericsystem.kernel;

import java.util.Arrays;

import org.testng.annotations.Test;

@Test
public class WeakEquivTest extends AbstractTest {

	public void test001_weakEquiv_Relation_SingularConstraint() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(0);
		Vertex myBmw = car.addInstance("myBmw");
		Vertex green = color.addInstance("green");
		Vertex myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		Vertex yellow = color.addInstance("yellow");
		assert !myBmwGreen.weakEquiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test001_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(0);
		carColor.enableReferentialIntegrity(0);
		Vertex myBmw = car.addInstance("myBmw");
		Vertex green = color.addInstance("green");
		Vertex yellow = color.addInstance("yellow");
		Vertex myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		assert myBmwGreen.weakEquiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test001_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_axeOne() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(1);
		carColor.enableReferentialIntegrity(1);
		Vertex myBmw = car.addInstance("myBmw");
		Vertex green = color.addInstance("green");
		Vertex yellow = color.addInstance("yellow");
		Vertex myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		assert myBmwGreen.weakEquiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}
}
