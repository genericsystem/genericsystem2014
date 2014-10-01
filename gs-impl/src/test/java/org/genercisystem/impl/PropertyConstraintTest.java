package org.genercisystem.impl;

import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.impl.Engine;
import org.genericsystem.impl.Generic;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	public void test001_enablePropertyConstraint_addInstance() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
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
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
		Generic subPower = engine.addInstance(power, "SubPower", vehicle);
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
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = engine.addInstance("Power", vehicle);
		Generic subPower = engine.addInstance(power, "Power", car);
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
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		power.setInstance("126", vehicle);
		assert power.getInstances().size() == 1;
		power.getInstances().forEach(x -> x.getValue().equals("126"));
	}

	public void test001_disablePropertyConstraint_setInstance() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
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
