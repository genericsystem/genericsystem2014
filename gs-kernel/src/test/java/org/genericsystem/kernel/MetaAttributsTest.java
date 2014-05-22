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
		// assert root.getMetaAttributes(root).contains(metaAttribut) : root.getMetaAttributes(root);
	}
}
