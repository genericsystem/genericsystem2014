package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Root.ValueCache;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
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

public class Vertex extends AbstractVertex<Vertex> implements AncestorsService<Vertex>, DependenciesService<Vertex>, InheritanceService<Vertex>, BindingService<Vertex>, CompositesInheritanceService<Vertex>, FactoryService<Vertex>, DisplayService<Vertex>,
SystemPropertiesService, ExceptionAdviserService<Vertex> {
	protected static Logger log = LoggerFactory.getLogger(Vertex.class);
	protected static final Vertex[] EMPTY_VERTICES = new Vertex[] {};
	private final Dependencies<Vertex> instances;
	private final Dependencies<Vertex> inheritings;
	private final CompositesDependencies<Vertex> metaComposites;
	private final CompositesDependencies<Vertex> superComposites;

	Vertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
		super(meta, null, value, components);
		if (isRoot())
			((Root) this).valueCache = new ValueCache();
		// this.meta = isRoot() ? this : meta;
		this.value = ((Root) getRoot()).getCachedValue(value);
		this.components = new Vertex[components.length];
		for (int i = 0; i < components.length; i++)
			this.components[i] = components[i] == null ? this : components[i];
		instances = buildDependencies();
		inheritings = buildDependencies();
		metaComposites = buildCompositeDependencies();
		superComposites = buildCompositeDependencies();

		checkIsAlive(this.meta);
		checkAreAlive(overrides);
		checkAreAlive(super.components);
		super.supers = getSupers(overrides).toArray(Vertex[]::new);
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
	public Dependencies<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<Vertex> getInheritings() {
		return inheritings;
	}

	@Override
	public Vertex getInstance(Serializable value, Vertex... components) {
		return build(this, Arrays.stream(getEmptyArray()), value, Arrays.stream(components)).getAlive();
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
	public Vertex[] getEmptyArray() {
		return EMPTY_VERTICES;
	}

	@Override
	public Snapshot<Vertex> getInheritings(final Vertex origin, final int level) {
		return new AbstractSnapshot<Vertex>() {
			@Override
			public Iterator<Vertex> iterator() {
				return inheritingsIterator(origin, level);
			}
		};
	}

	private class Forbidden extends HashSet<Vertex> {

		private static final long serialVersionUID = 1877502935577170921L;

		private final Map<Vertex, Collection<Vertex>> inheritings = new HashMap<>();

		private final Vertex origin;
		private final int level;

		public Forbidden(Vertex origin, int level) {
			this.origin = origin;
			this.level = level;
		}

		private Iterator<Vertex> inheritanceIterator() {
			return getInheringsStream(Vertex.this).iterator();
		};

		private Stream<Vertex> getInheringsStream(Vertex superVertex) {
			Collection<Vertex> result = inheritings.get(superVertex);
			if (result == null)
				inheritings.put(superVertex, result = new Inheritings(superVertex).inheritanceStream().collect(Collectors.toList()));
			return result.stream();
		}

		class Inheritings {

			private final Vertex base;

			private Inheritings(Vertex base) {
				this.base = base;
			}

			private boolean isTerminal() {
				return base.equals(Vertex.this);
			}

			protected Stream<Vertex> inheritanceStream() {
				return projectStream(fromAboveStream());
			}

			private Stream<Vertex> supersStream() {
				return base.getSupersStream().filter(next -> base.getMeta().equals(next.getMeta()) && origin.isAttributeOf(next));
			}

			private Stream<Vertex> fromAboveStream() {
				if (!origin.isAttributeOf(base))
					return Stream.empty();
				Stream<Vertex> supersStream = supersStream();
				if (!supersStream().iterator().hasNext())
					return (base.isRoot() || !origin.isAttributeOf(base.getMeta())) ? Stream.of(origin) : getInheringsStream(base.getMeta());
					return Statics.concat(supersStream, superVertex -> getInheringsStream(superVertex)).distinct();
			}

			protected Stream<Vertex> projectStream(Stream<Vertex> streamToProject) {
				return Statics.concat(streamToProject, holder -> getStream(holder)).distinct();
			}

			protected Stream<Vertex> getStream(final Vertex holder) {
				if (holder.getLevel() != level || base.getSuperComposites(holder).iterator().hasNext())
					add(holder);
				Stream<Vertex> indexStream = Stream.concat(holder.getLevel() < level ? base.getMetaComposites(holder).stream() : Stream.empty(), base.getSuperComposites(holder).stream());
				return Stream.concat(isTerminal() && contains(holder) ? Stream.empty() : Stream.of(holder), projectStream(indexStream));
			}
		}
	}

	public Iterator<Vertex> inheritingsIterator(final Vertex origin, final int level) {

		return new Forbidden(origin, level).inheritanceIterator();
	}

}
