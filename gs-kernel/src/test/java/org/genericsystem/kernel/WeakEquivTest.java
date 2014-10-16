package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.exception.ExistsException;
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
		assert myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test002_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity() {
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
		assert !myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test003_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_axeOne() {
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
		assert !myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test004_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_supers() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(Statics.TARGET_POSITION);
		carColor.disableReferentialIntegrity(Statics.TARGET_POSITION);
		assert !carColor.isReferentialIntegrityEnabled(Statics.TARGET_POSITION);
		Vertex myBmw = car.addInstance("myBmw");
		Vertex green = color.addInstance("green");
		Vertex yellow = color.addInstance("yellow");
		Vertex myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);

		catchAndCheckCause(() -> carColor.addInstance(myBmwGreen, "myBmwGreen2", myBmw, green), ExistsException.class);
	}

	public void test005_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_setInstance() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(1);
		carColor.disableReferentialIntegrity(1);
		Vertex myBmw = car.addInstance("myBmw");
		Vertex green = color.addInstance("green");
		Vertex yellow = color.addInstance("yellow");
		Vertex myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		Vertex myBmwGreen2 = carColor.setInstance("myBmwGreen2", myBmw, green);
		assert !myBmwGreen.isAlive();
	}

	public void test006_weakEquiv_DefaultValue() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex defaultColor = color.addInstance("Red", car);
		Vertex carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(0);
		carColor.enableReferentialIntegrity(0);
		Vertex myBmw = engine.addInstance(car, "myBmw");
		Vertex green = color.addInstance("green");
		Vertex myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		assert !myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, defaultColor));
	}
}
