package org.genericsystem.kernel;

import java.util.Arrays;
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
		Vertex html = tree.addRoot("html");
		assert html.getMeta().equals(tree);
		assert html.getComposites().contains(html);
		assert html.getComposites().size() == 1;
		assert html.getSupers().isEmpty();
	}

	public void test004() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex html = tree.addRoot("html");
		Vertex head = html.addSubNode("head");
		Vertex body = html.addSubNode("body");
		Vertex div = body.addSubNode("div");

		assert !html.getSubNodes().contains(html);
		assert html.getSubNodes().containsAll(Arrays.asList(head, body));
		assert html.getSubNodes().size() == 2;
		assert html.getAllSubNodes().containsAll(Arrays.asList(html, head, body, div));
		assert html.getAllSubNodes().size() == 4;

		assert head.getSubNodes().isEmpty();
		assert head.getAllSubNodes().contains(head);
		assert head.getAllSubNodes().size() == 1;

		assert body.getSubNodes().contains(div);
		assert body.getSubNodes().size() == 1;
		assert body.getAllSubNodes().containsAll(Arrays.asList(body, div));
		assert body.getAllSubNodes().size() == 2;

		assert div.getSubNodes().isEmpty();
		assert div.getAllSubNodes().contains(div);
		assert div.getAllSubNodes().size() == 1;

	}

	public void test005() {
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
