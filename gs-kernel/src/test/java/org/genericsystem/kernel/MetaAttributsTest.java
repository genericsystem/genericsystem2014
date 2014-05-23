package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MetaAttributsTest extends AbstractTest {

	public void testMetaAttributInstanciation() {
		Root root = new Root();

		final String metaAttributValue = "metaAttribut";// Statics.ENGINE_VALUE;
		MetaAttribut metaAttribut = new MetaAttribut(root, metaAttributValue);
		assert root.isAlive();
		assert root.isMeta();
		assert metaAttributValue.equals(metaAttribut.getValue());
		assert root.getMetaAttributes(root).size() == 1;
		assert metaAttribut.isAlive();
		assert metaAttribut.getValue().equals(metaAttributValue);
		assert metaAttribut.getComponentsStream().count() == 1;
		assert metaAttribut.getComponents().contains(root);
		assert metaAttribut.isMeta();
		log.info(metaAttribut.info());
		log.info(root.info());
		// assert root.getMetaAttributes(root).contains(metaAttribut) : root.getMetaAttributes(root);
	}

	public void testMetaAttributInstanciationWith2LevelsOfInstantiation() {
		Root root = new Root();
		final String metaAttributValue = "metaAttribut";
		MetaAttribut metaAttribut = new MetaAttribut(root, metaAttributValue);
		Vertex vehicle = root.addInstance("vehicle");
		Vertex power = metaAttribut.addInstance("Power", vehicle);
		assert vehicle.getAttributes(metaAttribut).size() == 1;
		assert vehicle.getAttributes(metaAttribut).contains(power);
		assert power.isInstanceOf(metaAttribut);
		assert metaAttribut.isAncestorOf(power);
	}
}
