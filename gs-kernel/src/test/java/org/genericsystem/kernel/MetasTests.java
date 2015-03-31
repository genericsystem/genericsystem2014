package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MetasTests extends AbstractTest {

	public void test001() {
		Root engine = new Root();

		Generic metaAttribute = engine.getMetaAttribute();
		assert metaAttribute == engine.adjustMeta(engine);
		assert metaAttribute.getMeta() == metaAttribute;
		assert metaAttribute.isMeta();
		assert metaAttribute.getBaseComponent().equals(engine);
		assert metaAttribute.inheritsFrom(engine);
		Generic metaRelation = engine.getMetaAttribute().getInheritings().first();
		assert metaRelation == engine.adjustMeta(engine, engine);
		assert metaRelation.isMeta();
		assert metaRelation.getBaseComponent().equals(engine);
		assert metaRelation.getTargetComponent().equals(engine);
		assert metaRelation.inheritsFrom(metaAttribute);
	}

	public void test002() {
		Root engine = new Root();
		assert engine.getCurrentCache().getMeta(1).equals(engine.getMetaAttribute());
		assert engine.getCurrentCache().getMeta(5) == null;
		Generic pentaMeta = engine.getCurrentCache().setMeta(5);
		assert engine.getCurrentCache().getMeta(5) == pentaMeta;
		assert pentaMeta == engine.getCurrentCache().setMeta(5);
		assert pentaMeta.equals(engine.getCurrentCache().getMeta(5));
		assert engine.getCurrentCache().getMeta(3) == null;
		Generic ternaryMeta = engine.getCurrentCache().setMeta(3);
		assert !pentaMeta.isAlive();
		assert engine.getCurrentCache().getMeta(5).inheritsFrom(ternaryMeta);
	}

	public void test004() {
		Root engine = new Root();
		assert engine.setInstance(engine.getValue(), engine).equals(engine.getMetaAttribute());
		assert engine.setInstance(engine.getValue(), engine, engine).equals(engine.getMetaRelation());
		assert engine.setInstance(engine.getValue(), engine, engine, engine).equals(engine.getCurrentCache().getMeta(3));
	}
}
