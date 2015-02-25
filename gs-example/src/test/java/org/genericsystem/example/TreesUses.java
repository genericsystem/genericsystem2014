package org.genericsystem.example;

import java.util.Arrays;

import org.genericsystem.api.core.IVertex.Visitor;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class TreesUses {
	public void simpleTree() {
		Engine engine = new Engine();

		// Create a tree called Html5Tags
		Generic html5Tags = engine.addInstance("Html5Tags");

		// Create the root html
		Generic html = html5Tags.addInstance("html");

		// Create the child header
		html5Tags.addInstance(html, "header");
		// Create the child body
		Generic body = html5Tags.addInstance(html, "body");
		// Create the child footer
		html5Tags.addInstance(html, "footer");

		// Create the child p
		html5Tags.addInstance(body, "p");
		// Create the child table
		html5Tags.addInstance(body, "table");

		// Persist changes
		engine.getCurrentCache().flush();
	}

	public void traverseTree() {
		Engine engine = new Engine();

		Generic html5Tags = engine.addInstance("Html5Tags");

		Generic html = html5Tags.addInstance("html");

		html5Tags.addInstance(html, "header");
		Generic body = html5Tags.addInstance(html, "body");

		html5Tags.addInstance(body, "p");
		html5Tags.addInstance(body, "table");

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

		// Persist changes
		engine.getCurrentCache().flush();
	}

	public void binaryTree() {
		Engine engine = new Engine();

		// Create a binary tree called GenealogicTree
		Generic genealogicTree = engine.addInstance("GenealogicTree");

		// Create the root father
		Generic father = genealogicTree.addInstance("father");
		// Create the root mother
		Generic mother = genealogicTree.addInstance("mother");

		// Create the child son
		Generic son = genealogicTree.addInstance(Arrays.asList(father, mother), "son");
		// Create the child daughter
		Generic daughter = genealogicTree.addInstance(Arrays.asList(father, mother), "daughter");

		// Get children of father
		assert father.getInheritings().containsAll(Arrays.asList(son, daughter));

		// Get children of mother
		assert mother.getInheritings().containsAll(Arrays.asList(son, daughter));

		// Persist changes
		engine.getCurrentCache().flush();
	}
}
