package org.genericsystem.example;

import java.util.Arrays;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.IVertex.Visitor;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class TreesUses {
	public void simpleTree() {
		Engine engine = new Engine();

		// Create a tree called Html5Tags
		Generic html5Tags = engine.addTree("Html5Tags");

		// Create the root html
		Generic html = html5Tags.addRoot("html");

		// Create the child header
		html.addChild("header");
		// Create the child body
		Generic body = html.addChild("body");
		// Create the child footer
		html.addChild("footer");

		// Create the child p
		body.addChild("p");
		// Create the child table
		body.addChild("table");

		// Commit changes
		engine.getCurrentCache().flush();
	}

	public void inheritingNodes() {
		Engine engine = new Engine();

		// Create a tree called Html5Tags
		Generic html5Tags = engine.addTree("Html5Tags");

		// Create the root html
		Generic html = html5Tags.addRoot("html");

		// Create the child header
		Generic header = html.addInheritingChild("header");
		// Create the child body
		Generic body = html.addInheritingChild("body");
		// Create the child footer
		Generic footer = html.addInheritingChild("footer");

		// Create the child p
		body.addInheritingChild("p");
		// Create the child table
		body.addInheritingChild("table");

		// Create a type Color
		Generic color = engine.addInstance("Color");
		// Create some instances of Color
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic yellow = color.addInstance("yellow");

		// Create the relation HtmlTagsColor between Html5Tags and Color
		Generic htmlTagsColor = html5Tags.addRelation("HtmlTagsColor", color);
		// Enable singular constraint on the base
		htmlTagsColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		// Associate the Color red to the htmlTags html
		html.addLink(htmlTagsColor, "htmlRed", red);
		// Associate the Color blue to the htmlTags header
		header.addLink(htmlTagsColor, "headerBlue", blue);
		// Associate the Color yellow to the htmlTags footer
		footer.addLink(htmlTagsColor, "footerYellow", yellow);
		// No explicitly associated Color to the htmlTags body

		// Get color of the htmlTags html
		assert html.getLink(htmlTagsColor, "htmlRed").getTargetComponent().equals(red);
		// Get color of the htmlTags header
		assert header.getLink(htmlTagsColor, "headerBlue").getTargetComponent().equals(blue);
		// Get color of the htmlTags footer
		assert footer.getLink(htmlTagsColor, "footerYellow").getTargetComponent().equals(yellow);
		// Get color of the htmlTags body
		assert body.getLink(htmlTagsColor, "htmlRed").getTargetComponent().equals(red);

		// Commit changes
		engine.getCurrentCache().flush();
	}

	public void traverseTree() {
		Engine engine = new Engine();

		Generic html5Tags = engine.addTree("Html5Tags");

		Generic html = html5Tags.addRoot("html");

		html.addChild("header");
		Generic body = html.addChild("body");
		html.addChild("footer");

		body.addChild("p");
		body.addChild("table");

		// Pass through the tree from html
		html.traverse(new Visitor<Generic>() {
			@Override
			public void before(Generic node) {
				System.out.println("before : " + node.getValue());
			}

			@Override
			public void after(Generic node) {
				System.out.println("after : " + node.getValue());
			}
		});

		// Commit changes
		engine.getCurrentCache().flush();
	}

	public void binaryTree() {
		Engine engine = new Engine();

		// Create a binary tree called GenealogicTree
		Generic genealogicTree = engine.addTree("GenealogicTree", 2);

		// Create the root father
		Generic father = genealogicTree.addRoot("father");
		// Create the root mother
		Generic mother = genealogicTree.addRoot("mother");

		// Create the child son
		Generic son = father.addChild("son", mother);
		// Create the child daughter
		Generic daughter = father.addChild("daughter", mother);

		// Get children of father
		assert father.getChildren().containsAll(Arrays.asList(son, daughter));

		// Get children of mother
		assert mother.getChildren().containsAll(Arrays.asList(son, daughter));

		// Commit changes
		engine.getCurrentCache().flush();
	}
}
