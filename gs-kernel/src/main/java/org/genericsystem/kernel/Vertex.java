package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Root.ValueCache;
import org.genericsystem.kernel.exceptions.NotAliveException;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.BindingService;
import org.genericsystem.kernel.services.CompositesInheritanceService;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;
import org.genericsystem.kernel.services.FactoryService;
import org.genericsystem.kernel.services.InheritanceService;
import org.genericsystem.kernel.services.SystemPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex implements AncestorsService<Vertex>, DependenciesService<Vertex>, InheritanceService<Vertex>, BindingService<Vertex>, CompositesInheritanceService, FactoryService<Vertex>, DisplayService<Vertex>, SystemPropertiesService,
		ExceptionAdviserService<Vertex> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Serializable value;
	private final Vertex meta;
	private final Vertex[] components;
	private final Dependencies<Vertex> instances;
	private final Dependencies<Vertex> inheritings;
	private final CompositesDependencies<Vertex> metaComposites;
	private final CompositesDependencies<Vertex> superComposites;
	private final Vertex[] supers;

	// Engine constructor
	Vertex() {
		((Root) this).valueCache = new ValueCache();
		meta = this;
		value = ((Root) this).getCachedValue(Statics.ENGINE_VALUE);
		components = Statics.EMPTY_VERTICES;
		instances = buildDependencies();
		inheritings = buildDependencies();
		metaComposites = buildCompositeDependencies();
		superComposites = buildCompositeDependencies();
		supers = Statics.EMPTY_VERTICES;
	}

	public Vertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		this.meta = isRoot() ? this : meta;
		this.value = ((Root) getRoot()).getCachedValue(value);
		this.components = new Vertex[components.length];
		for (int i = 0; i < components.length; i++)
			this.components[i] = components[i] == null ? this : components[i];
		instances = buildDependencies();
		inheritings = buildDependencies();
		metaComposites = buildCompositeDependencies();
		superComposites = buildCompositeDependencies();

		checkIsAlive(meta);
		checkAreAlive(overrides);
		checkAreAlive(components);
		supers = getSupers(overrides).toArray(Vertex[]::new);
		checkOverrides(overrides);
		checkSupers();
	}

	@Override
	public Vertex build(Vertex meta, Stream<Vertex> overrides, Serializable value, Stream<Vertex> components) {
		return new Vertex(meta, overrides.toArray(Vertex[]::new), value, components.toArray(Vertex[]::new));
	}

	private void checkAreAlive(Vertex... vertices) {
		Arrays.stream(vertices).forEach(this::checkIsAlive);
	}

	private void checkIsAlive(Vertex vertex) {
		if (!vertex.isAlive())
			rollbackAndThrowException(new NotAliveException(vertex.info()));
	}

	@Override
	public Vertex getMeta() {
		return meta;
	}

	@Override
	public Vertex[] getComponents() {
		return components;
	}

	@Override
	public Stream<Vertex> getComponentsStream() {
		return Arrays.stream(components);
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public Dependencies<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<Vertex> getInheritings() {
		return inheritings;
	}

	public Snapshot<Vertex> getMetaComposites(Vertex meta) {
		return metaComposites.getByIndex(meta);
	}

	public Snapshot<Vertex> getSuperComposites(Vertex superVertex) {
		return superComposites.getByIndex(superVertex);
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
	public Stream<Vertex> getSupersStream() {
		return Arrays.stream(supers);
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	@Override
	public Vertex[] getEmptyArray() {
		return Statics.EMPTY_VERTICES;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (!(obj instanceof Vertex))
	// return false;
	// Vertex service = (Vertex) obj;
	// return this.equiv(service);
	// }
	//
	// @Override
	// public int hashCode() {
	// // TODO introduce : meta and components length
	// return Objects.hashCode(getValue());
	// }

}
