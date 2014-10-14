package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Vertex tree = root.addInstance("Tree", new Vertex[] { null });
		assert root.getMetaAttribute().equals(tree.getMeta());
		assert tree.getComposites().contains(tree);
		assert tree.getComposites().size() == 1;
		assert tree.getSupers().isEmpty();
	}
}
