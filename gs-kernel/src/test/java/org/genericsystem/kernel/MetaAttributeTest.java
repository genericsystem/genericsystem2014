package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;

@Test
public class MetaAttributeTest extends AbstractTest {

	public void testInstantiationOfMetaAttribute() {
		Root engine = new Root();
		Vertex metaAttribute = engine.addInstance(engine, Statics.ENGINE_VALUE, engine);
		assert engine.getLevel() == 0;
		assert metaAttribute.getLevel() == 0;
	}

	public void testHolderWithoutMetaAttribut() {
		// Given
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(vehicle, "Car");
		Vertex power = root.addInstance("Power", car);
		Vertex myCar = car.addInstance("myCar");

		// When
		Vertex v333 = power.addInstance(333, myCar);

		// Then
		assert myCar.getHolders(power).contains(v333);
		assert myCar.getHolders(power).size() == 1;
	}

	public void testHolderWithMetaAttribut() {
		// Given
		Root engine = new Root();
		Vertex metaAttribute = engine.addInstance(engine, Statics.ENGINE_VALUE, engine);
		assert metaAttribute.inheritsFrom(engine) : metaAttribute.info();

		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power = engine.addInstance("Power", car);
		Vertex myCar = car.addInstance("myCar");

		// When
		Vertex v333 = power.addInstance(333, myCar);

		// Then
		assert myCar.getHolders(power).contains(v333);
		assert myCar.getHolders(power).size() == 1;
	}

	public void testMetaAttributeWithInheritance() {
		// Given
		Root engine = new Root();
		Vertex metaAttribute = engine.addInstance(engine, Statics.ENGINE_VALUE, engine);

		Vertex vehicle = engine.addInstance("Vehicle");
		assert metaAttribute.isMetaOf(metaAttribute, Collections.emptyList(), "Power", Arrays.asList(vehicle));
		Vertex power = engine.addInstance("Power", vehicle);
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex myCar = car.addInstance("myCar");
		assert engine.getLevel() == 0;
		assert metaAttribute.getLevel() == 0;
		assert power.getMeta().equals(metaAttribute);
		assert power.isInstanceOf(metaAttribute) : power.info();
	}
}
