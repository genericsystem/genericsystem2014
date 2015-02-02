package org.genericsystem.mutability;

import java.util.Arrays;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.IVertex.Visitor;
import org.testng.annotations.Test;

@Test
public class TreeTest {
	public void testSimpleTree() {
		Engine engine = new Engine();

		Generic html5Tags = engine.addTree("Html5Tags");

		Generic html = html5Tags.addRoot("html");

		Generic header = html.addChild("header");
		Generic body = html.addChild("body");
		Generic footer = html.addChild("footer");

		Generic p = body.addChild("p");
		Generic table = body.addChild("table");

		assert html5Tags.getInstances().containsAll(Arrays.asList(html, header, body, footer, p, table)) : html5Tags.getInstances();
		assert html.getChildren().containsAll(Arrays.asList(header, body, footer)) : html.getChildren();
		assert header.getChildren().isEmpty() : header.getChildren();
		assert body.getChildren().containsAll(Arrays.asList(p, table)) : body.getChildren();
		assert footer.getChildren().isEmpty() : footer.getChildren();
		assert p.getChildren().isEmpty() : p.getChildren();
		assert table.getChildren().isEmpty() : table.getChildren();
	}

	public void testInheritingNodes() {
		Engine engine = new Engine();

		Generic html5Tags = engine.addTree("Html5Tags");

		Generic html = html5Tags.addRoot("html");

		Generic header = html.addInheritingChild("header");
		Generic body = html.addInheritingChild("body");
		Generic footer = html.addInheritingChild("footer");

		Generic p = body.addInheritingChild("p");
		Generic table = body.addInheritingChild("table");

		assert html5Tags.getInstances().containsAll(Arrays.asList(html, header, body, footer, p, table)) : html5Tags.getInstances();
		assert html.getChildren().containsAll(Arrays.asList(header, body, footer)) : html.getChildren();
		assert header.getChildren().isEmpty() : header.getChildren();
		assert body.getChildren().containsAll(Arrays.asList(p, table)) : body.getChildren();
		assert footer.getChildren().isEmpty() : footer.getChildren();
		assert p.getChildren().isEmpty() : p.getChildren();
		assert table.getChildren().isEmpty() : table.getChildren();

		assert header.inheritsFrom(html) : header.getSupers();
		assert body.inheritsFrom(html) : body.getSupers();
		assert footer.inheritsFrom(html) : footer.getSupers();
		assert p.inheritsFrom(body) && p.inheritsFrom(html) : p.getSupers();
		assert table.inheritsFrom(body) && p.inheritsFrom(html) : p.getSupers();

		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic yellow = color.addInstance("yellow");

		Generic htmlTagsColor = html5Tags.addRelation("HtmlTagsColor", color);
		htmlTagsColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		html.addLink(htmlTagsColor, "htmlRed", red);
		header.addLink(htmlTagsColor, "headerBlue", blue);
		footer.addLink(htmlTagsColor, "footerYellow", yellow);
		// No explicitly associated Color to the htmlTags body

		assert html.getLink(htmlTagsColor, "htmlRed").getTargetComponent().equals(red) : html.getLink(htmlTagsColor, "htmlRed").getTargetComponent();
		assert header.getLink(htmlTagsColor, "headerBlue").getTargetComponent().equals(blue) : header.getLink(htmlTagsColor, "headerBlue").getTargetComponent();
		assert footer.getLink(htmlTagsColor, "footerYellow").getTargetComponent().equals(yellow) : footer.getLink(htmlTagsColor, "footerYellow").getTargetComponent();
		assert body.getLink(htmlTagsColor, "htmlRed").getTargetComponent().equals(red) : body.getLink(htmlTagsColor, "htmlRed").getTargetComponent();
	}

	public void testTraverseTree() {
		Engine engine = new Engine();

		Generic html5Tags = engine.addTree("Html5Tags");

		Generic html = html5Tags.addRoot("html");

		html.addChild("header");
		Generic body = html.addChild("body");
		html.addChild("footer");

		body.addChild("p");
		body.addChild("table");

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

	public void testBinaryTree() {
		Engine engine = new Engine();

		Generic genealogicTree = engine.addTree("GenealogicTree", 2);

		Generic father = genealogicTree.addRoot("father");
		Generic mother = genealogicTree.addRoot("mother");

		Generic son = father.addChild("son", mother);
		Generic daughter = father.addChild("daughter", mother);

		assert genealogicTree.getInstances().containsAll(Arrays.asList(father, mother, son, daughter)) : genealogicTree.getInstances();
		assert father.getChildren().containsAll(Arrays.asList(son, daughter)) : father.getChildren();
		assert mother.getChildren().containsAll(Arrays.asList(son, daughter)) : mother.getChildren();
		assert son.getChildren().isEmpty() : son.getChildren();
		assert daughter.getChildren().isEmpty() : daughter.getChildren();
	}
}
