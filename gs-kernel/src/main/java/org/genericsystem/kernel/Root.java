package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;

public class Root extends Vertex implements RootService<Vertex> {

	public Root() {
		this(Statics.ENGINE_VALUE);
	}

	public Root(Serializable value) {
		init(0, null, Collections.emptyList(), value, Collections.emptyList());
	}

	// @Override
	// public Vertex getRoot() {
	// return this;
	// }
	//
	// @Override
	// public Vertex getAlive() {
	// return this;
	// }

}
