package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.ExtendedSignature;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.BindingService;
import org.genericsystem.kernel.services.CompositesInheritanceService;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;
import org.genericsystem.kernel.services.InheritanceService;
import org.genericsystem.kernel.services.RestructuratorService;
import org.genericsystem.kernel.services.SystemPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertexConcurrency extends ExtendedSignature<VertexConcurrency> implements AncestorsConcurrencyService<VertexConcurrency>, DependenciesService<VertexConcurrency>, InheritanceService<VertexConcurrency>, BindingService<VertexConcurrency>,
		CompositesInheritanceService<VertexConcurrency>, FactoryConcurrencyService<VertexConcurrency>, DisplayService<VertexConcurrency>, SystemPropertiesService<VertexConcurrency>, ExceptionAdviserService<VertexConcurrency>,
		RestructuratorService<VertexConcurrency> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private LifeManager lifeManager;

	private final Dependencies<VertexConcurrency> instances = buildDependencies();
	private final Dependencies<VertexConcurrency> inheritings = buildDependencies();
	private final CompositesDependencies<VertexConcurrency> superComposites = buildCompositeDependencies();
	private final CompositesDependencies<VertexConcurrency> metaComposites = buildCompositeDependencies();

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs == null ? ((RootConcurrency) getRoot()).pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public VertexConcurrency buildInstance() {
		return new VertexConcurrency();
	}

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	@Override
	public Dependencies<VertexConcurrency> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<VertexConcurrency> getInheritings() {
		return inheritings;
	}

	// TODO what a pity to build a total Vertex with its dependencies only to call equiv in getAlive()
	// equiv need only AncestorService as parameter
	@Override
	public VertexConcurrency getInstance(Serializable value, VertexConcurrency... components) {
		return buildInstance(Collections.emptyList(), value, Arrays.asList(components)).getAlive();
	}

	@Override
	public CompositesDependencies<VertexConcurrency> getMetaComposites() {
		return metaComposites;
	}

	@Override
	public CompositesDependencies<VertexConcurrency> getSuperComposites() {
		return superComposites;
	}

	@Override
	public void rollback() {
		getRoot().rollback();
	}
}
