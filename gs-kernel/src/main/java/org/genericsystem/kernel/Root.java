package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Root extends Vertex implements RootService<Vertex> {

	public Root() {
		this(Statics.ENGINE_VALUE);
	}

	public Root(Serializable value) {
		init(0, null, Collections.emptyList(), value, Collections.emptyList());
	}

	// TODO KK
	Vertex setMetaAttribute(Vertex... components) {
		checkSameEngine(Arrays.asList(components));
		Vertex allComponents[] = Statics.insertIntoArray(this, components, 0);
		Vertex instance = getInstance(getRoot().getValue(), allComponents);
		if (instance != null)
			return instance;
		List<Vertex> supersList = new ArrayList<>(new SupersComputer<>(0, meta, Collections.emptyList(), getRoot().getValue(), Arrays.asList(allComponents)));
		Vertex meta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		return meta.buildInstance().init(0, meta, supersList, getRoot().getValue(), Arrays.asList(allComponents)).plug();
	}

	@Override
	public Vertex getRoot() {
		return this;
	}

	@Override
	public Vertex getAlive() {
		return  this;
	}

}
