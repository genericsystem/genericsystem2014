package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.ExistsException;
import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		assert root.getMetaAttribute().equals(tree.getMeta());
		assert tree.getComponents().contains(null) : tree.getComponents();
		assert tree.getComponents().size() == 1;
		assert tree.getSupers().isEmpty();

		// TODO: Ajouter un atrribut sur une instance
	}

	public void test002() {
		Root root = new Root();
		Vertex tree = root.addTree("Tree");
		Vertex rootNode = tree.addRoot("rootNode");

		assert tree.equals(rootNode.getMeta()) : rootNode.detailedInfo();
		assert rootNode.getComponents().contains(null);
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
		assert html.getComponents().contains(null);
		assert html.getComponents().size() == 1;
		assert html.getSupers().isEmpty();
	}

	public void test004() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex html = tree.addRoot("html");
		Vertex head = html.addChild("head");
		Vertex body = html.addChild("body");
		Vertex div = body.addChild("div");

		assert !html.getChildren().contains(html);
		assert html.getChildren().containsAll(Arrays.asList(head, body)) : html.getChildren().info();
		assert html.getChildren().size() == 2;
		assert html.getAllChildren().containsAll(Arrays.asList(html, head, body, div));
		assert html.getAllChildren().size() == 4;

		assert head.getChildren().isEmpty();
		assert head.getAllChildren().contains(head);
		assert head.getAllChildren().size() == 1;

		assert body.getChildren().contains(div);
		assert body.getChildren().size() == 1;
		assert body.getAllChildren().containsAll(Arrays.asList(body, div));
		assert body.getAllChildren().size() == 2;

		assert div.getChildren().isEmpty();
		assert div.getAllChildren().contains(div);
		assert div.getAllChildren().size() == 1;

	}

	public void test005() {
		Root root = new Root();
		Vertex tree = root.addTree("tree");
		Vertex rootNode = tree.addRoot("rootNode");
		Vertex htmlNode = rootNode.addChild("htmlNode");
		Vertex bodyNode = htmlNode.addChild("bodyNode");
		Vertex divNode = bodyNode.addChild("divNode");
		Vertex formNode = divNode.addChild("formNode");

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
		Vertex color = root.addInstance("Color");
		Vertex treeColor = tree.addAttribute("TreeColor", color);

		Vertex blue = color.addInstance("blue");
		Vertex red = color.addInstance("red");
		Vertex green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Vertex html = tree.addRoot("html");
		html.setHolder(treeColor, "htmlIsRed", red);
		Vertex head = html.addChild("head");
		Vertex body = html.addChild("body");
		Vertex div = body.addChild("div");
		div.setHolder(treeColor, "divIsGreen", green);

		assert tree.getHolders(treeColor).first().getTargetComponent().equals(blue);
		assert html.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert head.getHolders(treeColor).first().getTargetComponent().equals(blue);
		assert body.getHolders(treeColor).first().getTargetComponent().equals(blue);
		assert div.getHolders(treeColor).first().getTargetComponent().equals(green);
	}

	public void test008() {
		Root root = new Root();

		Vertex tree = root.addTree("Tree");
		Vertex color = root.addInstance("Color");
		Vertex treeColor = tree.addAttribute("TreeColor", color);

		Vertex blue = color.addInstance("blue");
		Vertex red = color.addInstance("red");
		Vertex green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Vertex html = tree.addRoot("html");
		html.setHolder(treeColor, "htmlIsRed", red);
		Vertex head = html.addInheritingChild("head");
		Vertex body = html.addInheritingChild("body");
		Vertex div = body.addInheritingChild("div");
		div.setHolder(treeColor, "divIsGreen", green);

		assert tree.getHolders(treeColor).first().getTargetComponent().equals(blue);
		assert html.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert head.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert body.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert div.getHolders(treeColor).first().getTargetComponent().equals(green);
	}

	public void testInheritanceTree() {
		Root root = new Root(Statics.ENGINE_VALUE);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.addInheritingChild("Child");
		rootTree.addInheritingChild("Child2");
		child.addInheritingChild("Child3");
	}

	public void testSetInheritanceTree() {
		Root root = new Root(Statics.ENGINE_VALUE);
		Vertex tree = root.addTree("Tree");
		Vertex rootTree = tree.addRoot("Root");
		Vertex child = rootTree.setInheritingChild("Child");
		rootTree.setInheritingChild("Child2");
		child.setInheritingChild("Child3");
	}

	public void testInheritingTree() {
		Root engine = new Root();

		Vertex graphicComponent = engine.addTree("graphicComponent");

		Vertex webPage = graphicComponent.addRoot("webPage");

		Vertex header = webPage.addInheritingChild("header");
		Vertex body = webPage.addInheritingChild("body");

		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex blue = color.addInstance("blue");

		Vertex graphicComponentColor = graphicComponent.addRelation("graphicComponentColor", color);
		graphicComponentColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		webPage.addLink(graphicComponentColor, "webPageRed", red);
		header.addLink(graphicComponentColor, "headerBlue", blue);
		
		assert webPage.getLink(graphicComponentColor, "webPageRed").getTargetComponent().equals(red);
		assert header.getLink(graphicComponentColor, "headerBlue").getTargetComponent().equals(blue);
		assert body.getLink(graphicComponentColor, "webPageRed").getTargetComponent().equals(red);
	}

}
