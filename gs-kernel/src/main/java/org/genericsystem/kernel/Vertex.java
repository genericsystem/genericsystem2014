package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.services.Removable;
import org.genericsystem.kernel.services.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends ExtendedSignature<Vertex> implements Removable<Vertex>, Updatable<Vertex> {
	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies(null);
	private final Dependencies<Vertex> inheritings = buildDependencies(null);
	private final CompositesDependencies<Vertex> superComposites = buildCompositeDependencies(null);
	private final CompositesDependencies<Vertex> metaComposites = buildCompositeDependencies(null);

	@Override
	public Vertex buildInstance() {
		return new Vertex();
	}

	@Override
	public Dependencies<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<Vertex> getInheritings() {
		return inheritings;
	}

	// TODO what a pity to build a total Vertex with its dependencies only to call equiv in getAlive()
	// equiv need only AncestorService as parameter
	@Override
	public Vertex getInstance(Serializable value, Vertex... components) {
		return buildInstance(Collections.emptyList(), value, Arrays.asList(components)).getAlive();
	}

	@Override
	public CompositesDependencies<Vertex> getMetaComposites() {
		return metaComposites;
	}

	@Override
	public CompositesDependencies<Vertex> getSuperComposites() {
		return superComposites;
	}

	@Override
	public void rollback() {
		getRoot().rollback();
	}

}
