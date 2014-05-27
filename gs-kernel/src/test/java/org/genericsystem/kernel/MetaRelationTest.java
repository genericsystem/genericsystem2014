package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MetaRelationTest extends AbstractTest {

	public void testMetaRelation() {

		Root engine = new Root();
		Vertex metaAttribute = engine.setMetaAttribute();
		Vertex metaRelation = engine.setMetaAttribute(engine);
		assert metaRelation.getMeta().equals(metaAttribute);
		assert metaRelation.inheritsFrom(metaAttribute);
	}

	public void testInstantiationOfMetaRelation() {

		Root engine = new Root();
		Vertex metaAttribute = engine.setMetaAttribute();
		Vertex metaRelation = engine.setMetaAttribute(engine);
		Vertex car = engine.addInstance("Car");
		Vertex power = engine.addInstance("Power", car);
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("carColor", new Vertex[] { car, color });
		assert carColor.isInstanceOf(metaRelation);
		assert power.isInstanceOf(metaAttribute);
	}
}
