package org.genercisystem.impl;

import org.testng.annotations.Test;

public class TestDefaultMethods extends AbstractTest {

	private static interface BaseService<T extends BaseService<T>> {
		T test();
	}

	private static interface NodeService<T extends NodeService<T>> extends BaseService<T> {
		@Override
		T test();
	}

	private static interface RootService<T extends NodeService<T>> extends NodeService<T> {
		@Override
		default T test() {
			log.info("RootService");
			return null;
		}
	}

	private static abstract class Node implements NodeService<Node> {

	}

	private static class Root extends Node implements BaseService<Node>, RootService<Node> {

	}

	@Test
	public void testDefaultMethodeOnRoot() {
		new Root().test();
	}

}
