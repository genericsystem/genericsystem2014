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
		Vertex rootNode = tree.addRoot("rootNode");

		assert tree.equals(rootNode.getMeta()) : rootNode.detailedInfo();
		assert rootNode.getComposites().contains(rootNode);
		assert rootNode.getComposites().size() == 1;
		assert rootNode.getSupers().isEmpty();

		assert tree.getInstances().contains(rootNode);
		assert tree.getInstances().size() == 1;
		assert tree.getAllInstances().contains(rootNode) : tree.getAllInstances().stream().collect(Collectors.toList());
		assert tree.getAllInstances().size() == 1;
		assert rootNode.getSupers().isEmpty();
	}

	public void test003() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex rootNode = tree.addRoot("rootNode");
		Vertex htmlNode = rootNode.addSubNode("htmlNode");

		assert tree.equals(htmlNode.getMeta());
		assert htmlNode.getComposites().contains(rootNode) : htmlNode.detailedInfo();
		assert htmlNode.getComposites().size() == 1;
		assert htmlNode.getSupers().isEmpty();
		assert rootNode.getHolders(tree).contains(htmlNode);
		assert rootNode.getHolders(tree).size() == 1 : rootNode.getHolders(tree).info();

	}

	public void test004() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex rootNode = tree.addRoot("rootNode");
		Vertex htmlNode = rootNode.addSubNode("htmlNode");
		Vertex bodyNode = htmlNode.addSubNode("bodyNode");
		Vertex divNode = bodyNode.addSubNode("divNode");
		Vertex formNode = divNode.addSubNode("formNode");

		assert tree.getAllInstances().contains(rootNode);
		assert tree.getAllInstances().contains(htmlNode);
		assert tree.getAllInstances().contains(bodyNode);
		assert tree.getAllInstances().contains(divNode);
		assert tree.getAllInstances().contains(formNode);
		assert tree.getAllInstances().size() == 5;
	}
}
