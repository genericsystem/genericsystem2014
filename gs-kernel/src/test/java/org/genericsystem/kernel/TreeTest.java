package org.genericsystem.kernel;

import java.util.stream.Collectors;

import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		assert root.getMetaAttribute().equals(tree.getMeta());
		assert tree.getComposites().contains(tree);
		assert tree.getComposites().size() == 1;
		assert tree.getSupers().isEmpty();

		// TODO: Ajouter un atrribut sur une instance
	}

	public void test002() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		Vertex treeRoot = tree.addNode("treeRoot");

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

	public void test003() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex rootNode = tree.addRoot("nodeRoot");
		Vertex htmlNode = rootNode.addNode("htmlNode");
		Vertex htmlNode2 = rootNode.addNode("htmlNode2");

		assert tree.getAllInstances().contains(rootNode);
		assert tree.getAllInstances().size() == 1;

		assert rootNode.getAllInstances().contains(htmlNode);
		assert rootNode.getAllInstances().size() == 1;

		assert rootNode.getSupers().isEmpty();
		assert htmlNode.getSupers().contains(rootNode);
		assert htmlNode.getSupers().size() == 1;

	}
}
