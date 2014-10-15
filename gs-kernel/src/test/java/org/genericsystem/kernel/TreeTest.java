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
		// Vertex headNode = htmlNode.addNode("headNode");

		assert tree.equals(htmlNode.getMeta());
		assert htmlNode.getComposites().contains(rootNode) : htmlNode.detailedInfo();
		assert htmlNode.getComposites().size() == 1;
		assert htmlNode.getSupers().isEmpty();
		assert rootNode.getHolders(tree).contains(htmlNode);
		assert rootNode.getHolders(tree).contains(htmlNode);
		assert rootNode.getHolders(tree).size() == 1 : rootNode.getHolders(tree).info();

		// assert rootNode.getHolders(tree).contains(htmlNode);
		// assert rootNode.getAllInstances().size() == 1;
		// assert htmlNode.getAllInstances().contains(headNode) : htmlNode.getComponents().stream().collect(Collectors.toList());
		// assert htmlNode.getAllInstances().size() == 1;
		//
		// assert false : htmlNode.detailedInfo();
		// assert false : headNode.detailedInfo();
		// assert headNode.getComponents().size() == 1;
	}
}
