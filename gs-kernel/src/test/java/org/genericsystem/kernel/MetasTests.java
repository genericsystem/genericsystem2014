package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

@Test
public class MetasTests extends AbstractTest {

	public void test001() {
		Root engine = new Root();

		Vertex metaAttribute = engine.getMetaAttribute();
		assert metaAttribute == engine.adjustMeta(engine.getValue(), Arrays.asList(engine));
		assert metaAttribute.getMeta() == metaAttribute;
		assert metaAttribute.isMeta();
		assert metaAttribute.getBaseComponent().equals(engine);
		assert metaAttribute.inheritsFrom(engine);
		Vertex metaRelation = engine.getMetaAttribute().getInheritings().first();
		assert metaRelation == engine.adjustMeta(engine.getValue(), Arrays.asList(engine, engine));
		assert metaRelation.isMeta();
		assert metaRelation.getBaseComponent().equals(engine);
		assert metaRelation.getTargetComponent().equals(engine);
		assert metaRelation.inheritsFrom(metaAttribute);
	}

	public void test002() {
		Root engine = new Root();
		assert engine.getMeta(1).equals(engine.getMetaAttribute());
		assert engine.getMeta(5) == null;
		Vertex pentaMeta = engine.setMeta(5);
		assert engine.getMeta(5) == pentaMeta;
		assert pentaMeta == engine.setMeta(5);
		assert pentaMeta.equals(engine.getMeta(5));
		assert engine.getMeta(3) == null;
		Vertex ternaryMeta = engine.setMeta(3);
		assert !pentaMeta.isAlive();
		assert engine.getMeta(5).inheritsFrom(ternaryMeta);
	}

	public void test003() {
		Root engine = new Root();
		assert engine.getMeta(1).equals(engine.getMetaAttribute());
		List<Vertex> supers = new ArrayList<>(new SupersComputer<>(engine, null, Collections.singletonList(engine.getMetaAttribute()), engine.getValue(), Arrays.asList(engine, engine, engine)));
		assert supers.contains(engine.getMeta(2));
		assert supers.size() == 1 : supers.stream().map(x -> x.detailedInfo()).collect(Collectors.toList());
	};
}
