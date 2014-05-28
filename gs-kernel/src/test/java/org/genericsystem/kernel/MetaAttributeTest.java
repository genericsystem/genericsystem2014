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
}
