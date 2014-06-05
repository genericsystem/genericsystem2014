package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MetaAttributeTest extends AbstractTest {

	public void test001_setMetaAttribute_engine() {
		Root engine = new Root();
		Vertex metaAttribute = engine.setMetaAttribute();
		assert engine.getLevel() == 0;
		assert metaAttribute.getLevel() == 0;
		assert metaAttribute.inheritsFrom(engine) : metaAttribute.info();
	}

	public void test002_setMetaAttribute_attribute() {
		Root engine = new Root();
		Vertex metaAttribute = engine.setMetaAttribute();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power", vehicle);
		assert power.getMeta() == metaAttribute;
		assert power.isInstanceOf(metaAttribute) : power.info();
	}

	public void test003_setMetaAttribute_override() {
		Root engine = new Root();
		Vertex metaAttribute = engine.setMetaAttribute();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex option = engine.addInstance("Option", vehicle);
		Vertex power = engine.addInstance(option, "Power", vehicle);

		assert power.getMeta() == metaAttribute;
		assert power.isInstanceOf(metaAttribute) : power.info();
	}

	public void test004_setMetaAttribute_override() {
		Root engine = new Root();
		Vertex metaAttribute = engine.setMetaAttribute();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power", vehicle);
		Vertex power2 = engine.addInstance(power, "Power2", vehicle);

		assert power2.getMeta() == metaAttribute;
		assert power2.isInstanceOf(metaAttribute) : power2.info();
		assert power2.getSupersStream().anyMatch(superVertex -> superVertex.equals(power)) : power2.info();
	}
}
