package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.genericsystem.api.core.IVertex.Visitor;
import org.genericsystem.api.core.exceptions.ExistsException;
import org.testng.annotations.Test;

@Test
public class TreeTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Generic tree = root.addInstance("Tree");
		assert tree.getSupers().isEmpty();
	}

	public void test002() {
		Root root = new Root();
		Generic tree = root.addInstance("Tree");
		Generic rootNode = tree.addInstance("rootNode");

		assert tree.equals(rootNode.getMeta()) : rootNode.detailedInfo();
		assert rootNode.getSupers().isEmpty();

		assert tree.getInstances().contains(rootNode);
		assert tree.getInstances().size() == 1;
		assert tree.getAllInstances().contains(rootNode) : tree.getAllInstances().get().collect(Collectors.toList());
		assert tree.getAllInstances().size() == 1;
		assert rootNode.getSupers().isEmpty();
	}

	public void test003() {
		Root root = new Root();
		Generic tree = root.addInstance("tree");
		Generic html = tree.addInstance("html");
		assert html.getMeta().equals(tree);
		assert html.getSupers().isEmpty();
	}

	public void test004() {
		Root root = new Root();
		Generic tree = root.addInstance("tree");
		Generic html = tree.addInstance("html");
		Generic head = tree.addInstance(html, "head");
		Generic body = tree.addInstance(html, "body");
		Generic div = tree.addInstance(body, "div");

		assert !html.getInheritings().contains(html);
		assert html.getInheritings().containsAll(Arrays.asList(head, body)) : html.getInheritings().info();
		assert html.getInheritings().size() == 2;
		assert html.getAllInheritings().containsAll(Arrays.asList(html, head, body, div));
		assert html.getAllInheritings().size() == 4;

		assert head.getInheritings().isEmpty();
		assert head.getAllInheritings().contains(head);
		assert head.getAllInheritings().size() == 1;

		assert body.getInheritings().contains(div);
		assert body.getInheritings().size() == 1;
		assert body.getAllInheritings().containsAll(Arrays.asList(body, div));
		assert body.getAllInheritings().size() == 2;

		assert div.getInheritings().isEmpty();
		assert div.getAllInheritings().contains(div);
		assert div.getAllInheritings().size() == 1;

	}

	public void test005() {
		Root root = new Root();
		Generic tree = root.addInstance("tree");
		Generic rootNode = tree.addInstance("rootNode");
		Generic htmlNode = tree.addInstance(rootNode, "htmlNode");
		Generic bodyNode = tree.addInstance(htmlNode, "bodyNode");
		Generic divNode = tree.addInstance(bodyNode, "divNode");
		Generic formNode = tree.addInstance(divNode, "formNode");

		assert tree.getAllInstances().contains(rootNode);
		assert tree.getAllInstances().contains(bodyNode);
		assert tree.getAllInstances().contains(divNode);
		assert tree.getAllInstances().contains(formNode);
		assert tree.getAllInstances().size() == 5;
	}

	public void test006() {
		Root root = new Root();
		root.addInstance("Tree");
		catchAndCheckCause(() -> root.addInstance("Tree"), ExistsException.class);
	}

	public void test007() {
		Root root = new Root();

		Generic tree = root.addInstance("Tree");
		Generic color = root.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);

		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Generic html = tree.addInstance("html");
		html.setHolder(treeColor, "htmlIsRed", red);
		Generic head = tree.addInstance(html, "head");
		Generic body = tree.addInstance(html, "body");
		Generic div = tree.addInstance(body, "div");
		div.setHolder(treeColor, "divIsGreen", green);

		assert tree.getHolders(treeColor).first().getTargetComponent().equals(blue);
		assert html.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert head.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert body.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert div.getHolders(treeColor).first().getTargetComponent().equals(green);
	}

	public void test008() {
		Root root = new Root();

		Generic tree = root.addInstance("Tree");
		Generic color = root.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);

		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Generic html = tree.addInstance("html");
		html.setHolder(treeColor, "htmlIsRed", red);
		Generic head = tree.addInstance(html, "head");
		Generic body = tree.addInstance(html, "body");
		Generic div = tree.addInstance(body, "div");
		div.setHolder(treeColor, "divIsGreen", green);

		assert tree.getHolders(treeColor).first().getTargetComponent().equals(blue);
		assert html.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert head.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert body.getHolders(treeColor).first().getTargetComponent().equals(red);
		assert div.getHolders(treeColor).first().getTargetComponent().equals(green);
	}

	public void testTraverseTree() {
		Root engine = new Root();

		Generic html5Tags = engine.addInstance("Html5Tags");

		Generic html = html5Tags.addInstance("html");

		html5Tags.addInstance(html, "header");
		Generic body = html5Tags.addInstance(html, "body");
		html5Tags.addInstance(html, "footer");

		html5Tags.addInstance(body, "p");
		html5Tags.addInstance(body, "table");

		int[] result = { 0 };

		html5Tags.traverse(new Visitor<Generic>() {
			@Override
			public void before(Generic node) {
				if (node.getValue().equals("html")) {
					result[0] += 1;
				} else if (node.getValue().equals("header") || node.getValue().equals("body") || node.getValue().equals("footer")) {
					result[0] += 2;
				} else if (node.getValue().equals("p") || node.getValue().equals("table")) {
					result[0] += 3;
				}
			}

			@Override
			public void after(Generic node) {
				if (node.getValue().equals("html")) {
					result[0] -= 1;
				} else if (node.getValue().equals("header") || node.getValue().equals("body") || node.getValue().equals("footer")) {
					result[0] -= 2;
				} else if (node.getValue().equals("p") || node.getValue().equals("table")) {
					result[0] -= 3;
				}
			}
		});

		assert result[0] == 0;
	}

	public void testTree1() {
		Root root = new Root();
		Generic a1 = root.addInstance("A");
		Generic b = root.addInstance(a1, "B");
		Generic a2 = root.addInstance(b, "A");
		assert root.getInstance("A").equals(a1);
		assert root.getInstance(Arrays.asList(b), "A").equals(a2);
	}

	public void testTree2() {
		Root root = new Root();
		Generic a1 = root.addInstance("A");
		Generic b = root.addInstance("B");
		Generic a2 = root.addInstance(b, "A");
		assert root.getInstance("A").equals(a1);
		assert root.getInstance(Arrays.asList(b), "A").equals(a2);
	}

	public void testTree3() {
		Root root = new Root();
		Generic b = root.addInstance("B");
		Generic c = root.addInstance("C");
		Generic a1 = root.addInstance(b, "A");
		Generic a2 = root.addInstance(c, "A");
		assert root.getInstance("A") == null : root.getInstance("A").info();
		assert root.getInstance(Arrays.asList(b), "A").equals(a1);
	}

}
