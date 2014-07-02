package org.genericsystem.kernel.archive;
//package org.genericsystem.kernel;
//
//import org.testng.annotations.Test;
//
//@Test
//public class SuperAttributeTest extends AbstractTest {
//
//	public void testSimpleSuperAttribute() {
//		Root engine = new Root();
//		// Vertex metaAttribute = engine.addInstance(engine, Statics.ENGINE_VALUE, engine);
//		Vertex superAttribute = engine.setMetaAttribute();// addInstance(engine, "SuperAttribute");
//		assert engine.getLevel() == 0;
//		assert superAttribute.getLevel() == 0;
//	}
//
//	public void testSuperAttribute() {
//		Root engine = new Root();
//		Vertex superAttribute = engine.addInstance(Statics.ENGINE_VALUE, engine);
//
//		assert superAttribute.getLevel() == 1;
//		Vertex vehicle = engine.addInstance("Vehicle");
//		Vertex power = engine.addInstance(superAttribute, "Power", vehicle);
//
//		log.info(power.info());
//		log.info(superAttribute.info());
//		assert superAttribute.getInheritings().size() == 1 : superAttribute.getInheritings().size();
//		assert superAttribute.getInheritings().contains(power) : superAttribute.getInheritings().size();
//		assert power.inheritsFrom(superAttribute);
//	}
// }
