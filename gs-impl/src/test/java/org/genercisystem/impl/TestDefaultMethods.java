package org.genercisystem.impl;

import org.testng.annotations.Test;

public class TestDefaultMethods extends AbstractTest {

	private static interface INode<T> {
		default T test() {
			log.info("INode");
			return null;
		}
	}

	private static interface IRoot extends INode<Node> {

		@Override
		default Node test() {
			log.info("IRoot");
			return null;
		}
	}

	private static abstract class Node implements INode<Node> {

	}

	private static class Root extends Node implements IRoot, INode<Node> {

	}

	private static class Root2 extends Node implements INode<Node>, IRoot {

	}

	@Test
	public void testInterfacesOrderChangeBehavior() {
		new Root().test();
		new Root2().test();
	}

}
