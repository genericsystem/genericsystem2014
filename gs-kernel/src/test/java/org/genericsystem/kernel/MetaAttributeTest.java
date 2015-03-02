package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MetaAttributeTest extends AbstractTest {

	public void test001_metaAttribute() {
		Root root = new Root();
		Generic metaAttribute = root.getMetaAttribute();
		assert metaAttribute == root.setInstance(root.getValue(), root);
		assert metaAttribute.getLevel() == 0;
		assert metaAttribute.isMeta();
		assert metaAttribute.getMeta() == metaAttribute;
		assert metaAttribute.inheritsFrom(root) : metaAttribute.info();
		assert metaAttribute.isInstanceOf(root);
		assert metaAttribute.isInstanceOf(metaAttribute);
		assert !root.getInstances().contains(metaAttribute);
		assert !root.getAllInstances().contains(metaAttribute);
	}

	public void test002_addInstance_metaAttribute() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
		assert power.getMeta() == metaAttribute;
		assert power.isInstanceOf(metaAttribute) : power.info();
	}

	public void test003_addInstance_metaAttribute_override() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
		Generic power2 = engine.addInstance(power, "Power2", vehicle);

		assert power2.getMeta() == metaAttribute;
		assert power2.isInstanceOf(metaAttribute) : power.info();
	}

	public void test004_addInstance_metaAttribute_override() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
		Generic power2 = engine.addInstance(power, "Power2", vehicle);

		assert power2.getMeta() == metaAttribute;
		assert power2.isInstanceOf(metaAttribute) : power2.info();
		assert power2.getSupers().stream().anyMatch(superVertex -> superVertex.equals(power)) : power2.info();
	}
}
