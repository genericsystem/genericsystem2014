package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.kernel.exceptions.ExistsException;
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

	public void test001_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_supers() {
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
		new RollbackCatcher() {

			@Override
			public void intercept() {
				Vertex myBmwGreen2 = carColor.addInstance(myBmwGreen, "myBmwGreen2", myBmw, green);
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test001_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_setInstance() {
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
		Vertex myBmwGreen2 = carColor.setInstance("myBmwGreen2", myBmw, green);
		assert !myBmwGreen.isAlive();
	}
}
