package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.genericsystem.api.exception.ExistsException;
import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		assert root.getMetaAttribute().equals(tree.getMeta());
		assert tree.getComponents().contains(tree);
		assert tree.getComponents().size() == 1;
		assert tree.getSupers().isEmpty();

		// TODO: Ajouter un atrribut sur une instance
	}

	public void test002() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		Vertex rootNode = tree.addRoot("rootNode");

		assert tree.equals(rootNode.getMeta()) : rootNode.detailedInfo();
		assert rootNode.getComponents().contains(rootNode);
		assert rootNode.getComponents().size() == 1;
		assert rootNode.getSupers().isEmpty();

		assert tree.getInstances().contains(rootNode);
		assert tree.getInstances().size() == 1;
		assert tree.getAllInstances().contains(rootNode) : tree.getAllInstances().get().collect(Collectors.toList());
		assert tree.getAllInstances().size() == 1;
		assert rootNode.getSupers().isEmpty();
	}

	public void test003() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex html = tree.addRoot("html");
		assert html.getMeta().equals(tree);
		assert html.getComponents().contains(html);
		assert html.getComponents().size() == 1;
		assert html.getSupers().isEmpty();
	}

	public void test004() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex html = tree.addRoot("html");
		Vertex head = html.addNode("head");
		Vertex body = html.addNode("body");
		Vertex div = body.addNode("div");

		assert !html.getSubNodes().contains(html);
		assert html.getSubNodes().containsAll(Arrays.asList(head, body)) : html.getSubNodes().info();
		assert html.getSubNodes().size() == 2;
		assert html.getAllNodes().containsAll(Arrays.asList(html, head, body, div));
		assert html.getAllNodes().size() == 4;

		assert head.getSubNodes().isEmpty();
		assert head.getAllNodes().contains(head);
		assert head.getAllNodes().size() == 1;

		assert body.getSubNodes().contains(div);
		assert body.getSubNodes().size() == 1;
		assert body.getAllNodes().containsAll(Arrays.asList(body, div));
		assert body.getAllNodes().size() == 2;

		assert div.getSubNodes().isEmpty();
		assert div.getAllNodes().contains(div);
		assert div.getAllNodes().size() == 1;

	}

	public void test005() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex rootNode = tree.addRoot("rootNode");
		Vertex htmlNode = rootNode.addNode("htmlNode");
		Vertex bodyNode = htmlNode.addNode("bodyNode");
		Vertex divNode = bodyNode.addNode("divNode");
		Vertex formNode = divNode.addNode("formNode");

		assert tree.getAllInstances().contains(rootNode);
		assert tree.getAllInstances().contains(htmlNode);
		assert tree.getAllInstances().contains(bodyNode);
		assert tree.getAllInstances().contains(divNode);
		assert tree.getAllInstances().contains(formNode);
		assert tree.getAllInstances().size() == 5;
	}

	public void test006() {
		Root root = new Root();
		root.addTree("Tree");
		catchAndCheckCause(() -> root.addTree("Tree"), ExistsException.class);
	}

	public void test007() {
		Root root = new Root();

		Vertex tree = root.addTree("Tree");
		Vertex color = root.addType("Color");
		Vertex treeColor = tree.addAttribute("TreeColor", color);

		Vertex blue = color.addInstance("blue");
		Vertex red = color.addInstance("red");
		Vertex green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Vertex html = tree.addRoot("html");
		html.setHolder(treeColor, "htmlIsRed", red);
		Vertex head = html.addNode("head");
		Vertex body = html.addNode("body");
		Vertex div = body.addNode("div");
		div.setHolder(treeColor, "divIsGreen", green);

		assert tree.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(blue);
		assert html.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(red);
		assert head.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(blue);
		assert body.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(blue);
		assert div.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(green);
	}

	public void test008() {
		Root root = new Root();

		Vertex tree = root.addTree("Tree");
		Vertex color = root.addType("Color");
		Vertex treeColor = tree.addAttribute("TreeColor", color);

		Vertex blue = color.addInstance("blue");
		Vertex red = color.addInstance("red");
		Vertex green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Vertex html = tree.addRoot("html");
		html.setHolder(treeColor, "htmlIsRed", red);
		Vertex head = html.addInheritingNode("head");
		Vertex body = html.addInheritingNode("body");
		Vertex div = body.addInheritingNode("div");
		div.setHolder(treeColor, "divIsGreen", green);

		assert tree.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(blue);
		assert html.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(red);
		assert head.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(red);
		assert body.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(red);
		assert div.getHolders(treeColor).get().findFirst().get().getComponents().get(Statics.TARGET_POSITION).equals(green);
	}

	public void testInheritanceTree() {
		Root root = new Root(Statics.ENGINE_VALUE);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.addInheritingNode("Child");
		rootTree.addInheritingNode("Child2");
		child.addInheritingNode("Child3");
	}

	public void testSetInheritanceTree() {
		Root root = new Root(Statics.ENGINE_VALUE);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.setInheritingNode("Child");
		rootTree.setInheritingNode("Child2");
		child.setInheritingNode("Child3");
	}

}
