package org.genericsystem.kernel;

import java.util.stream.Collectors;

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

	public void test002() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		Vertex treeRoot = tree.addInstance("treeRoot", new Vertex[] { null });

		assert tree.equals(treeRoot.getMeta());
		assert treeRoot.getComposites().contains(treeRoot);
		assert treeRoot.getComposites().size() == 1;
		assert treeRoot.getSupers().isEmpty();

		assert tree.getInstances().contains(treeRoot);
		assert tree.getInstances().size() == 1;
		assert tree.getAllInstances().contains(treeRoot) : tree.getAllInstances().stream().collect(Collectors.toList());
		assert tree.getAllInstances().size() == 1;
		assert treeRoot.getSupers().isEmpty();
	}

	// public void test003() {
	// Root root = new Root();
	// Vertex tree = root.addInstance("Tree", new Vertex[] { null });
	// Vertex treeRoot = tree.addInstance("treeRoot", new Vertex[] { null });
	// Vertex htmlNode = treeRoot.addAttribute("htmlNode");
	//
	// assert tree.getAllInstances().contains(treeRoot);
	// assert tree.getAllInstances().size() == 1;
	//
	// assert treeRoot.getAllInstances().contains(htmlNode);
	// assert treeRoot.getAllInstances().size() == 1;
	//
	// assert treeRoot.getSupers().isEmpty();
	// assert htmlNode.getSupers().contains(treeRoot);
	// assert htmlNode.getSupers().size() == 1;
	//
	// }
}
