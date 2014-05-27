package org.genericsystem.kernel;

import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;
import org.testng.annotations.Test;

@Test
public class MultipleRootsTest extends AbstractTest {

	public void testEnginesWithDifferentNames() {
		Root engine1 = new Root();
		String nameOfsecondEngine = "SecondEngine";
		Root engine2 = new Root(nameOfsecondEngine);

		assert engine1.getMeta().equals(engine1);
		assert engine1.getSupersStream().count() == 0;
		assert engine1.getComponentsStream().count() == 0;
		assert Statics.ENGINE_VALUE.equals(engine1.getValue());
		assert engine1.isAlive();

		assert engine2.getMeta().equals(engine2);
		assert engine2.getSupersStream().count() == 0;
		assert engine2.getComponentsStream().count() == 0;
		assert engine2.getValue().equals(nameOfsecondEngine);
		assert engine2.isAlive();

	}

	public void testEnginesWithDifferentNamesWithInstantiationAndAttribute1() {
		Root engine1 = new Root();
		Root engine2 = new Root("SecondEngine");

		Vertex car = engine1.addInstance("Car");
		Vertex car2 = engine2.addInstance("Car");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				Vertex power = engine1.addInstance("Power", car2);
			}
		}.assertIsCausedBy(CrossEnginesAssignementsException.class);
	}

	public void testEnginesWithDifferentNamesWithInstantiationAndAttribute2() {
		Root engine1 = new Root();
		Root engine2 = new Root("SecondEngine");

		Vertex car = engine1.addInstance("Car");
		Vertex car2 = engine2.addInstance("Car");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				Vertex power = engine2.addInstance("Power", car);
			}
		}.assertIsCausedBy(CrossEnginesAssignementsException.class);
	}

	public void testEnginesWithDifferentNamesWithInstantiationAndAttribute3() {
		Root engine1 = new Root("FirstEngine");
		Root engine2 = new Root("SecondEngine");

		Vertex car = engine1.addInstance("Car");
		Vertex car2 = engine2.addInstance("Car");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				Vertex power = engine2.addInstance("Power", car);
			}
		}.assertIsCausedBy(CrossEnginesAssignementsException.class);
	}

	public void testEnginesWithDifferentNamesWithMetaRelation() {
		Root engine1 = new Root();
		Root engine2 = new Root("SecondEngine");

		Vertex metaAttribute = engine2.setMetaAttribute();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Vertex metaRelation = engine2.setMetaAttribute(engine1);
			}
		}.assertIsCausedBy(CrossEnginesAssignementsException.class);
	}

	public void testEnginesWithDifferentNamesWithMetaRelation2() {
		Root engine1 = new Root();
		Root engine2 = new Root("SecondEngine");

		Vertex metaAttribute = engine2.setMetaAttribute();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Vertex metaRelation = engine2.setMetaAttribute(engine1);
			}
		}.assertIsCausedBy(CrossEnginesAssignementsException.class);
	}
}
