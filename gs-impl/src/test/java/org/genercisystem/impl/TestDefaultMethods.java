package org.genercisystem.impl;

import org.testng.annotations.Test;

public class TestDefaultMethods extends AbstractTest {

	private static interface NodeService<T> {
		T test();
	}

	private static interface RootService extends NodeService<Node> {

		@Override
		default Node test() {
			return null;
		}
	}

	private static abstract class Node implements NodeService<Node> {

	}

	private static class Root extends Node implements RootService, NodeService<Node> {

	}

	private static class Root2 extends Node implements NodeService<Node>, RootService {

	}

	@Test
	public void testInterfacesOrder() {
		new Root().test();
		new Root2().test();
	}

}
