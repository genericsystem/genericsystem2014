package org.genericsystem.kernel;

import org.genericsystem.api.exception.ExistsException;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	public void test001_enablePropertyConstraint_addInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.addInstance("123", vehicle);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				power.addInstance("126", vehicle);
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test002_enablePropertyConstraint_addInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
		Vertex subPower = Root.addInstance(power, "SubPower", vehicle);
		assert subPower.inheritsFrom(power);
		power.enablePropertyConstraint();
		assert subPower.isPropertyConstraintEnabled();
		subPower.addInstance("123", vehicle);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				subPower.addInstance("126", vehicle);
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test003_enablePropertyConstraint_addInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex car = Root.addInstance(vehicle, "Car");
		Vertex power = Root.addInstance("Power", vehicle);
		Vertex subPower = Root.addInstance(power, "Power", car);
		assert subPower.inheritsFrom(power);
		power.enablePropertyConstraint();
		assert subPower.isPropertyConstraintEnabled();
		subPower.addInstance("123", car);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				subPower.addInstance("126", car);
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test001_enablePropertyConstraint_setInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		power.setInstance("126", vehicle);
		assert power.getInstances().size() == 1;
		power.getInstances().forEach(x -> x.getValue().equals("126"));
	}

	public void test001_disablePropertyConstraint_setInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		power.setInstance("126", vehicle);
		assert power.getInstances().size() == 1;
		power.getInstances().forEach(x -> x.getValue().equals("126"));
		power.disablePropertyConstraint();
		assert !power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		assert power.getInstances().size() == 2;
	}

}
