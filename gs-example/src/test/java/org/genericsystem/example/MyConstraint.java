package org.genericsystem.example;

import java.io.Serializable;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Dependencies;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.Supers;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.AxedPropertyClassValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckedConstraint;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;
import org.testng.annotations.Test;

public class MyConstraint extends AbstractTest {

	@Test
	public void createByApi() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		vehicle.setSystemPropertyValue(InstanceSizeConstraint.class, ApiStatics.NO_POSITION, 0);
		catchAndCheckCause(() -> vehicle.addInstance("myVehicle"), ConstraintViolationException.class);
	}

	public static class InstanceSizeConstraint implements CheckedConstraint<org.genericsystem.cache.Generic> {

		@Override
		public void check(org.genericsystem.cache.Generic modified, org.genericsystem.cache.Generic constraintBase, Serializable value) throws ConstraintViolationException {
			if (constraintBase.getInstances().size() > (int) value)
				throw new ConstraintViolationException("Instance size of " + constraintBase.info() + " is more than " + value) {
				};
		}
	}

	@Test
	public void createByFullAnnot() {
		Engine engine = new Engine(Vehicle.class);
		Generic vehicle = engine.find(Vehicle.class);
		catchAndCheckCause(() -> vehicle.addInstance("myVehicle"), ConstraintViolationException.class);
	}

	@Dependencies({ DefaultInstanceSizeConstraint.class })
	public static class Vehicle {

	}

	@SystemGeneric
	@Meta(MetaAttribute.class)
	@Supers(SystemMap.class)
	@Components(Root.class)
	@AxedPropertyClassValue(propertyClass = InstanceSizeConstraint.class, pos = ApiStatics.NO_POSITION)
	@Dependencies({ DefaultValue.class })
	public static class DefaultInstanceSizeConstraint {
	}

	@SystemGeneric
	@Meta(DefaultInstanceSizeConstraint.class)
	@Components(Vehicle.class)
	@IntValue(0)
	public static class DefaultValue {
	}

	// @Test
	// public void createBySimpleFullAnnot() {
	// Engine engine = new Engine(Car.class);
	// Generic vehicle = engine.find(Car.class);
	// catchAndCheckCause(() -> vehicle.addInstance("myCar"), ConstraintViolationException.class);
	// }
	//
	// @Constraint(propertyClass = InstanceSizeConstraint.class, pos = ApiStatics.NO_POSITION)
	// public static class Car {
	//
	// }

}
