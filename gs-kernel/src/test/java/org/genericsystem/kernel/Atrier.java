//package org.genericsystem.kernel;
//
//import javax.management.relation.Relation;
//import javax.xml.ws.Holder;
//
//import org.genericsystem.kernel.AbstractTest.RollbackCatcher;
//
//public class Atrier {
//
//	public void testOneToManyDifferentValue() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//
//		Type car = cache.addType("Car");
//		Type tyre = cache.addType("Tyre");
//		final Relation carTyres = car.setRelation("CarTyres", tyre);
//
//		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
//		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);
//		carTyres.enablePropertyConstraint();
//		assert carTyres.isPropertyConstraintEnabled();
//
//		final Generic myBmw = car.addInstance("myBmw");
//		Generic frontLeft = tyre.addInstance("frontLeft");
//		Generic frontRight = tyre.addInstance("frontRight");
//		Generic rearLeft = tyre.addInstance("rearLeft");
//		final Generic rearRight = tyre.addInstance("rearRight");
//
//		myBmw.bind(carTyres, frontLeft);
//		myBmw.bind(carTyres, frontRight);
//		myBmw.bind(carTyres, rearLeft);
//
//		Link myBmwRearRight1 = myBmw.setLink(carTyres, "value1", rearRight);
//		Link myBmwRearRight2 = myBmw.setLink(carTyres, "value2", rearRight);
//
//		assert !myBmwRearRight1.isAlive();
//		assert myBmwRearRight2.isAlive();
//
//		assert myBmw.getLinks(carTyres).size() == 4 : myBmw.getLinks(carTyres);
//		assert equals((Snapshot) myBmw.getLinks(carTyres), "value1") == null;
//		assert equals((Snapshot) myBmw.getLinks(carTyres), "value2") != null;
//
//		carTyres.disablePropertyConstraint();
//		new RollbackCatcher() {
//			@Override
//			public void intercept() {
//				myBmw.setLink(carTyres, "value3", rearRight);
//			}
//		}.assertIsCausedBy(SingularConstraintViolationException.class);
//	}
//
//	public void testOneToManyManyToManyImpl() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//
//		Type car = cache.addType("Car");
//		Type passenger = cache.addType("Passenger");
//		final Relation carPassenger = car.setRelation("CarPassenger", passenger);
//		carPassenger.enableSingularConstraint(Statics.TARGET_POSITION);
//		assert carPassenger.isSingularConstraintEnabled(Statics.TARGET_POSITION);
//
//		final Generic myBmw = car.addInstance("myBmw");
//		final Generic michael = passenger.addInstance("michael");
//		Generic nicolas = passenger.addInstance("nicolas");
//
//		myBmw.setLink(carPassenger, "30%", michael);
//		myBmw.setLink(carPassenger, "40%", nicolas);
//		new RollbackCatcher() {
//			@Override
//			public void intercept() {
//				myBmw.setLink(carPassenger, "60%", michael);
//			}
//		}.assertIsCausedBy(SingularConstraintViolationException.class);
//	}
//
//	public void testOneToManyManyToManyImplTernary() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//
//		Type car = cache.addType("Car");
//		Type passenger = cache.addType("Passenger");
//		Type time = cache.addType("time");
//
//		final Relation carPassengerTime = car.setRelation("CarPassenger", passenger, time);
//		carPassengerTime.enableSingularConstraint(Statics.TARGET_POSITION);
//		assert carPassengerTime.isSingularConstraintEnabled(Statics.TARGET_POSITION);
//
//		final Generic myBmw = car.addInstance("myBmw");
//		final Generic yourAudi = car.addInstance("yourAudi");
//		final Generic michael = passenger.addInstance("michael");
//		final Generic today = time.addInstance("today");
//		Generic nicolas = passenger.addInstance("nicolas");
//
//		myBmw.setLink(carPassengerTime, "30%", michael, today);
//		myBmw.setLink(carPassengerTime, "40%", nicolas, today);
//		new RollbackCatcher() {
//			@Override
//			public void intercept() {
//				yourAudi.setLink(carPassengerTime, "60%", michael, today);
//			}
//		}.assertIsCausedBy(SingularConstraintViolationException.class);
//	}
//
//	public void testToOneDifferentValueReverse() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//
//		Type car = cache.addType("Car");
//		Type owner = cache.addType("Owner");
//		final Relation carOwner = car.setRelation("CarOwner", owner);
//		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
//		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);
//		carOwner.enableReferentialIntegrity();
//
//		final Generic myBmw = car.addInstance("myBmw");
//		final Generic me = owner.addInstance("me");
//		me.setLink(carOwner, "value1", myBmw);
//
//		new RollbackCatcher() {
//
//			@Override
//			public void intercept() {
//				cache.mountNewCache().start();
//				me.setLink(carOwner, "value2", myBmw);
//			}
//
//		}.assertIsCausedBy(SingularConstraintViolationException.class);
//
//		cache.start();
//		assert me.getLinks(carOwner).size() == 1 : me.getLinks(carOwner);
//		assert equals((Snapshot) myBmw.getLinks(carOwner), "value1") != null;
//		assert equals((Snapshot) myBmw.getLinks(carOwner), "value2") == null;
//		cache.flush();
//	}
//
//	public void testSingularTargetDefaultColor() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		final Type car = cache.addType("Car");
//		Type color = cache.addType("Color");
//
//		car.addInstance("myBmw");
//		car.addInstance("myAudi");
//		final Generic red = color.addInstance("red");
//
//		final Relation carColor = car.setRelation("CarColor", color);
//		carColor.enableSingularConstraint(Statics.TARGET_POSITION);
//
//		new RollbackCatcher() {
//
//			@Override
//			public void intercept() {
//				car.bind(carColor, red);
//				assert red.getLinks(carColor).size() == 2 : red.getLinks(carColor);
//			}
//		}.assertIsCausedBy(SingularConstraintViolationException.class);
//	}
//
//	public void singularForTargetAxe() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.addType("car");
//		Type color = cache.addType("color");
//		final Relation carColor = car.setRelation("carColor", color).enableSingularConstraint(Statics.TARGET_POSITION);
//		Generic myBmw = car.addInstance("myBmw");
//		final Generic myAudi = car.addInstance("myAudi");
//		final Generic yellow = color.addInstance("yellow");
//		myBmw.setLink(carColor, "myBmwYellow", yellow);
//		assert carColor.isSingularConstraintEnabled(Statics.TARGET_POSITION);
//		new RollbackCatcher() {
//			@Override
//			public void intercept() {
//				myAudi.setLink(carColor, "myAudiYellow", yellow);
//			}
//		}.assertIsCausedBy(SingularConstraintViolationException.class);
//	}
//
//	public void testMixin5() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//
//		Type window = cache.addType("Window");
//		Attribute windowSize = window.addAttribute("Size");
//		Holder defaultWindowSize = window.addValue(windowSize, 235);
//
//		Type resizable = cache.addType("Resizable");
//		Attribute resizableSize = resizable.addAttribute("Size");
//		// Holder defaultVehicleSize = resizable.addValue(resizableSize, 100);
//
//		/* -------------------------------------------------------------------------------------- */
//
//		Type resizableWindow = window.addSubType("resizableWindow", new Generic[] { resizable });
//		Attribute resizableWindowSize = resizableWindow.addAttribute("Size");
//
//		assert resizableWindow.getAttribute("Size").equals(resizableWindowSize);
//
//		Generic myResizableWindow = resizableWindow.addInstance("myResizableWindow");
//		// Generic myResizableWindow233 = myResizableWindow.setValue(windowSize, 150);
//		// || Generic myResizableWindow233 = myResizableWindow.addValue(resizableSize,150);
//
//		assert myResizableWindow.getValue(windowSize).equals(235);
//		assert myResizableWindow.getValue(resizableWindowSize).equals(235);
//		assert myResizableWindow.getValue(resizableSize).equals(235);
//	}
//
//	public void testMixin8() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.addType("Vehicle");
//		Attribute vehiclePower = vehicle.addAttribute("Power");
//		Holder defaultVehiclePower = vehicle.addValue(vehiclePower, 123);
//		Type car = vehicle.addSubType("Car");
//		Attribute carPower = ((GenericImpl) car).addSubAttribute(vehiclePower, "Power");
//
//		assert car.getValue(vehiclePower).equals(123);
//
//		// Holder defaultCarPower = car.addValue(carPower, 123);// automatic
//		assert car.getHolder(carPower).inheritsFrom(defaultVehiclePower);
//
//		Generic myCar = car.addInstance("myCar");
//		assert myCar.getValue(vehiclePower).equals(123);
//
//		assert car.getValue(vehiclePower).equals(123);
//		assert car.getValue(carPower).equals(123);
//		assert myCar.getValue(carPower).equals(123);
//	}
//
// }
