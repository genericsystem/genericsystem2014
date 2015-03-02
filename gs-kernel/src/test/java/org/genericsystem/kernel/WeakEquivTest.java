package org.genericsystem.kernel;

import java.util.Arrays;
import org.genericsystem.api.core.ApiStatics;
import org.testng.annotations.Test;

@Test
public class WeakEquivTest extends AbstractTest {

	public void test001_weakEquiv_Relation_SingularConstraint() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(0);
		Generic myBmw = car.addInstance("myBmw");
		Generic green = color.addInstance("green");
		Generic myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		Generic yellow = color.addInstance("yellow");
		assert myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test002_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(0);
		carColor.enableReferentialIntegrity(0);
		Generic myBmw = car.addInstance("myBmw");
		Generic green = color.addInstance("green");
		Generic yellow = color.addInstance("yellow");
		Generic myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		assert !myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test003_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_axeOne() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(1);
		carColor.enableReferentialIntegrity(1);
		Generic myBmw = car.addInstance("myBmw");
		Generic green = color.addInstance("green");
		Generic yellow = color.addInstance("yellow");
		Generic myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		assert !myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, yellow));
	}

	public void test004_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_supers() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addAttribute("CarColor", color);
		carColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);
		// carColor.disableReferentialIntegrity(Statics.TARGET_POSITION);
		assert engine.getMetaAttribute().isReferentialIntegrityEnabled(ApiStatics.TARGET_POSITION);
		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		Generic green = color.addInstance("green");
		Generic yellow = color.addInstance("yellow");
		Generic myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		// catchAndCheckCause(() -> carColor.addInstance(myBmwGreen, "myAudiGreen", myBmw, green), SingularConstraintViolationException.class);
		carColor.addInstance(myBmwGreen, "myAudiGreen", myBmw, green);
	}

	public void test005_weakEquiv_Relation_SingularConstraintAndReferencialIntegrity_setInstance() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(1);
		carColor.disableReferentialIntegrity(1);
		Generic myBmw = car.addInstance("myBmw");
		Generic green = color.addInstance("green");
		Generic yellow = color.addInstance("yellow");
		Generic myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		Generic myBmwGreen2 = carColor.setInstance("myBmwGreen2", myBmw, green);
		assert !myBmwGreen.isAlive();
	}

	public void test006_weakEquiv_DefaultValue() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic defaultColor = color.addInstance("Red", car);
		Generic carColor = engine.addInstance("CarColor", car, color);
		carColor.enableSingularConstraint(0);
		carColor.enableReferentialIntegrity(0);
		Generic myBmw = car.addInstance("myBmw");
		Generic green = color.addInstance("green", myBmw);
		Generic myBmwGreen = carColor.addInstance("myBmwGreen", myBmw, green);
		assert !myBmwGreen.equiv(carColor, "myBmwYellow", Arrays.asList(myBmw, defaultColor));
	}
}
