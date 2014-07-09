package org.genercisystem.impl;

import org.testng.annotations.Test;

public class TestDefaultMethods extends AbstractTest {

	private static interface NodeService<T> {
		default T test() {
			log.info("NodeService");
			return null;
		}
	}

	private static interface RootService extends NodeService<Node> {
		@Override
		default Node test() {
			log.info("RootService");
			return null;
		}
	}

	private static class Node implements NodeService<Node> {

	}

	private static class Root extends Node implements NodeService<Node>, RootService {

	}

	@Test
	public void testDefaultMethodeOnRoot() {
		new Root().test();
	}

}
