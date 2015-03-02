package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MetaRelationTest extends AbstractTest {

	public void test001_metaRelation() {
		Root engine = new Root();
		Generic metaRelation = engine.getMetaRelation();
		assert metaRelation.getLevel() == 0;
		assert metaRelation.isMeta();
		assert metaRelation.getMeta() == metaRelation;
		assert metaRelation.inheritsFrom(engine);
		assert metaRelation.isInstanceOf(engine);
		assert metaRelation.isInstanceOf(metaRelation);
		assert !engine.getInstances().contains(metaRelation);
		assert !engine.getAllInstances().contains(metaRelation);
	}

	public void test002_addInstance_metaRelation() {
		Root engine = new Root();
		Generic metaRelation = engine.getMetaRelation();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("carColor", new Generic[] { car, color });
		assert carColor.getMeta() == metaRelation;
		assert carColor.isInstanceOf(metaRelation);
	}
}
