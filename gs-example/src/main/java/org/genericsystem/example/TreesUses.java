package org.genericsystem.example;

import java.util.Arrays;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.IVertex.Visitor;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class TreesUses {
	public void notInheritingTree() {
		Engine engine = new Engine();

		// Create a binary tree
		Generic genealogicTree = engine.addTree("genealogicTree", 2);

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
	}

	public void inheritingTree() {
		Engine engine = new Engine();

		// Create a tree
		Generic webPage = engine.addTree("webPage");

		// Create the root html
		Generic html = webPage.addRoot("html");

		// Create the child header
		Generic header = html.addInheritingChild("header");
		// Create the child body
		Generic body = html.addInheritingChild("body");
		// Create the child footer
		Generic footer = html.addInheritingChild("footer");

		// Create the child text1
		body.addInheritingChild("text1");
		// Create the child text2
		body.addInheritingChild("text2");

		// Create a type Color
		Generic color = engine.addInstance("Color");
		// Create some instances of Color
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic yellow = color.addInstance("yellow");

		// Create the relation webPageComponentColor between webPage and Color
		Generic webPageComponentColor = webPage.addRelation("webPageComponentColor", color);
		// Enable singular constraint on the base
		webPageComponentColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		// Associate the color red to the webPageComponent html
		html.addLink(webPageComponentColor, "htmlRed", red);
		// Associate the color blue to the webPageComponent header
		header.addLink(webPageComponentColor, "headerBlue", blue);
		// Associate the color yellow to the webPageComponent footer
		footer.addLink(webPageComponentColor, "footerYellow", yellow);
		// No explicitly associated color to the webPageComponent body

		// Get color of the webPageComponent html
		assert html.getLink(webPageComponentColor, "htmlRed").getTargetComponent().equals(red);
		// Get color of the webPageComponent header
		assert header.getLink(webPageComponentColor, "headerBlue").getTargetComponent().equals(blue);
		// Get color of the webPageComponent footer
		assert footer.getLink(webPageComponentColor, "footerYellow").getTargetComponent().equals(yellow);
		// Get color of the webPageComponent body
		assert body.getLink(webPageComponentColor, "htmlRed").getTargetComponent().equals(red);
	}

	public void traverseTree() {
		Engine engine = new Engine();

		Generic webPage = engine.addTree("webPage");

		Generic html = webPage.addRoot("html");

		html.addInheritingChild("header");
		Generic body = html.addInheritingChild("body");
		html.addInheritingChild("footer");

		body.addInheritingChild("text1");
		body.addInheritingChild("text2");

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
	}
}
