package org.genericsystem.kernel;

import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ComponentsOrderTest extends AbstractTest {

	public void test_addLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.addLink(carColor, "myCarColor", green);
	}

	public void test_addLink_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		green.addLink(carColor, "myCarColor", myCar);
	}

	public void test_addInstance() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test_addInstance_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		carColor.addInstance("myCarColor", green, myCar);
	}

	public void test005() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic green = color.addInstance("green");
		catchAndCheckCause(() -> green.addLink(carColor, "myCarColor", green), MetaRuleConstraintViolationException.class);
	}

	public void test006() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		final Generic carCar = engine.addInstance("CarCar", car, car);
		final Generic myCar = car.addInstance("myCar");
		final Generic myCar2 = car.addInstance("myCar2");
		myCar.addLink(carCar, "myCarCar", myCar2);
	}

	public void test_setLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.setLink(carColor, "myCarColor", green);
	}

	public void test_setLink_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		green.setLink(carColor, "myCarColor", myCar);
	}

	public void test_setInstance() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		carColor.setInstance("myCarColor", myCar, green);
	}

	public void test_setInstance_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		carColor.setInstance("myCarColor", green, myCar);
	}

	public void test_autoRelation_setLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		final Generic largerThan = engine.addInstance("largerThan", car, car);
		final Generic myBmw = car.addInstance("myBmw");
		final Generic myAudi = car.addInstance("myAudi");
		final Generic myMercedes = car.addInstance("myMercedes");
		final Generic myMercedes2 = car.addInstance("myMercedes2");
		myBmw.setLink(largerThan, "myBmwLargerThanMyAudi", myAudi);
		myMercedes.setLink(largerThan, "myMercedesLargerThanMyBmw", myBmw);
		myBmw.setLink(largerThan, "myBmwLargerThanMymyMercedes2", myMercedes2);
		List<Generic> smallerThanMyMbw = myBmw.getLinks(largerThan, ApiStatics.BASE_POSITION).get().map(Generic::getTargetComponent).collect(Collectors.toList());
		assert smallerThanMyMbw.size() == 2;
		assert smallerThanMyMbw.contains(myAudi);
		assert smallerThanMyMbw.contains(myMercedes2);
	}

	public void test_autoRelation_addLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		final Generic largerThan = engine.addInstance("largerThan", car, car);
		final Generic myBmw = car.addInstance("myBmw");
		final Generic myAudi = car.addInstance("myAudi");
		final Generic myMercedes = car.addInstance("myMercedes");
		final Generic myMercedes2 = car.addInstance("myMercedes2");
		myBmw.addLink(largerThan, "myBmwLargerThanMyAudi", myAudi);
		myMercedes.addLink(largerThan, "myMercedesLargerThanMyBmw", myBmw);
		myBmw.addLink(largerThan, "myBmwLargerThanMymyMercedes2", myMercedes2);
		List<Generic> smallerThanMyMbw = myBmw.getLinks(largerThan, ApiStatics.BASE_POSITION).get().map(Generic::getTargetComponent).collect(Collectors.toList());
		assert smallerThanMyMbw.size() == 2;
		assert smallerThanMyMbw.contains(myAudi);
		assert smallerThanMyMbw.contains(myMercedes2);
	}

	public void test_autoRelation_setInstance() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		final Generic largerThan = engine.addInstance("largerThan", car, car);
		final Generic myBmw = car.addInstance("myBmw");
		final Generic myAudi = car.addInstance("myAudi");
		final Generic myMercedes = car.addInstance("myMercedes");
		final Generic myMercedes2 = car.addInstance("myMercedes2");
		largerThan.setInstance("myBmwLargerThanMyAudi", myBmw, myAudi);
		largerThan.setInstance("myMercedesLargerThanMyBmw", myMercedes, myBmw);
		largerThan.setInstance("myBmwLargerThanMymyMercedes2", myBmw, myMercedes2);
		List<Generic> smallerThanMyMbw = myBmw.getLinks(largerThan, ApiStatics.BASE_POSITION).get().map(Generic::getTargetComponent).collect(Collectors.toList());
		assert smallerThanMyMbw.size() == 2;
		assert smallerThanMyMbw.contains(myAudi);
		assert smallerThanMyMbw.contains(myMercedes2);
	}

	public void test_autoRelation_addInstance() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		final Generic largerThan = engine.addInstance("largerThan", car, car);
		final Generic myBmw = car.addInstance("myBmw");
		final Generic myAudi = car.addInstance("myAudi");
		final Generic myMercedes = car.addInstance("myMercedes");
		final Generic myMercedes2 = car.addInstance("myMercedes2");
		largerThan.addInstance("myBmwLargerThanMyAudi", myBmw, myAudi);
		largerThan.addInstance("myMercedesLargerThanMyBmw", myMercedes, myBmw);
		largerThan.addInstance("myBmwLargerThanMymyMercedes2", myBmw, myMercedes2);
		List<Generic> smallerThanMyMbw = myBmw.getLinks(largerThan, ApiStatics.BASE_POSITION).get().map(Generic::getTargetComponent).collect(Collectors.toList());
		assert smallerThanMyMbw.size() == 2;
		assert smallerThanMyMbw.contains(myAudi);
		assert smallerThanMyMbw.contains(myMercedes2);
	}

}