package org.genericsystem.kernel;

import java.util.Objects;

import org.testng.annotations.Test;

@Test
public class NonHeritableTest extends AbstractTest {

	public void test001() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		Generic color = engine.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);

		treeColor.enableHeritable();
		assert treeColor.isHeritableEnabled();
	}

	public void test001_a() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		Generic color = engine.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);
		assert treeColor.isHeritableEnabled();
	}

	public void test001_b() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		Generic color = engine.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);
		assert treeColor.isHeritableEnabled();
		treeColor.disableHeritable();
		assert !treeColor.isHeritableEnabled();
		treeColor.enableHeritable();
		assert treeColor.isHeritableEnabled();
	}

	public void test001_c() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");

		assert tree.isHeritableEnabled();
		tree.disableHeritable();
		assert !tree.isHeritableEnabled();
		tree.enableHeritable();
		assert tree.isHeritableEnabled();
		tree.disableHeritable();
	}

	public void test001_e() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		tree.enableUniqueValueConstraint();
		assert tree.isUniqueValueEnabled();
		tree.disableUniqueValueConstraint();
		assert !tree.isUniqueValueEnabled();

		// assert tree.isHeritableEnabled();
		// tree.disableHeritable();
		// System.out.println(tree.getComposites().info());
		// assert !tree.isHeritableEnabled();
		// tree.enableHeritable();
		// System.out.println(tree.getComposites().info());
		// assert tree.isHeritableEnabled();
		// tree.disableHeritable();
		// System.out.println(tree.getComposites().info());
	}

	public void test001_d() {
		Root engine = new Root();
		Generic tree = engine.addInstance("Tree");
		tree.enableHeritable();
		assert tree.isHeritableEnabled();
		tree.disableHeritable();
		assert !tree.isHeritableEnabled();
	}

	public void test002() {
		Root engine = new Root();

		Generic tree = engine.addInstance("Tree");
		Generic color = engine.addInstance("Color");
		Generic treeColor = tree.addAttribute("TreeColor", color);

		treeColor.disableHeritable();

		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		tree.setHolder(treeColor, "treeIsBlueByDefault", blue);

		Generic html = tree.addInstance("html");
		Generic head = tree.addInstance(html, "head");
		Generic body = tree.addInstance(html, "body");
		Generic div = tree.addInstance(body, "div");

		html.setHolder(treeColor, "htmlIsRed", red);
		div.setHolder(treeColor, "divIsGreen", green);

		assert Objects.equals(tree.getHolders(treeColor).first().getTargetComponent(), blue);
		assert Objects.equals(head.getHolders(treeColor).first(), null);
		assert Objects.equals(body.getHolders(treeColor).first(), null);
		assert Objects.equals(div.getHolders(treeColor).first().getTargetComponent(), green);
	}
}
