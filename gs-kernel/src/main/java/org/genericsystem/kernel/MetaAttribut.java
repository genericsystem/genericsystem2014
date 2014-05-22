package org.genericsystem.kernel;

import java.util.Arrays;

public class MetaAttribut extends Vertex {

	public MetaAttribut(Root root, String value) {
		initFromSupers(root, Arrays.asList(root), value, Arrays.asList(root));
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public Vertex getAlive() {
		return this;
	}
}
