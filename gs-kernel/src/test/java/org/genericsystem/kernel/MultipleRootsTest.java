package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.exception.CrossEnginesAssignementsException;
import org.testng.annotations.Test;

@Test
public class MultipleRootsTest extends AbstractTest {

	public void test001_Root_name() {
		Root engine1 = new Root();
		String nameOfsecondEngine = "SecondEngine";
		Root engine2 = new Root(nameOfsecondEngine);
		assert engine1.getMeta().equals(engine1);
		assert engine1.getSupers().isEmpty();
		assert engine1.getComposites().isEmpty();
		assert Statics.ENGINE_VALUE.equals(engine1.getValue());
		assert engine1.isAlive();
		assert engine2.getMeta().equals(engine2);
		assert engine2.getSupers().isEmpty();
		assert engine2.getComposites().isEmpty();
		assert engine2.getValue().equals(nameOfsecondEngine);
		assert engine2.isAlive();
	}

	public void test002_addInstance_attribute() {
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

	public void test003_addInstance_attribute() {
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

	public void test004_addInstance_attribute() {
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

	// public void test005_setMetaAttribute_attribute() {
	// Root engine1 = new Root();
	// Root engine2 = new Root("SecondEngine");
	// Vertex metaAttribute = engine2.setMetaAttribute();
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// Vertex metaRelation = engine2.setMetaAttribute(engine1);
	// }
	// }.assertIsCausedBy(CrossEnginesAssignementsException.class);
	// }

	// public void test006_setMetaAttribute_attribute() {
	// Root engine1 = new Root();
	// Root engine2 = new Root("SecondEngine");
	// Vertex metaAttribute = engine2.setMetaAttribute();
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// Vertex metaRelation = engine2.setMetaAttribute(engine1);
	// }
	// }.assertIsCausedBy(CrossEnginesAssignementsException.class);
	// }

	public void test007_addInstance_overrides() {
		Root engine1 = new Root();
		Root engine2 = new Root("SecondEngine");
		Vertex car = engine2.addInstance("Car");
		Vertex robot = engine2.addInstance("Robot");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				Vertex transformer = engine1.addInstance(Arrays.asList(car, robot), "Transformer");
			}
		}.assertIsCausedBy(CrossEnginesAssignementsException.class);
	}
}
