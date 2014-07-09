package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.Collections;

import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends ExtendedSignature<Vertex> implements VertexService<Vertex> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> superComposites = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> metaComposites = buildDependencies();

	<T> Dependencies<T> buildDependencies() {
		return new DependenciesImpl<T>();
	}

	public Vertex buildMetaAttribute() {
		Vertex metaAttribute = buildInstance();
		Vertex root = getRoot();
		metaAttribute.meta = root;
		metaAttribute.value = root.value;
		metaAttribute.components = new ArrayList<Vertex>();
		metaAttribute.components.add(root);
		metaAttribute.supers = new ArrayList<Vertex>();
		metaAttribute.supers.add(root);
		return metaAttribute;
	};

	public Vertex buildSystemMap() {
		Vertex metaAttribute = buildMetaAttribute();
		metaAttribute.plug();
		Vertex root = getRoot();
		Vertex systemMap = buildInstance();
		systemMap.meta = metaAttribute;
		systemMap.value = SystemMap.class;
		systemMap.components = new ArrayList<Vertex>();
		systemMap.components.add(root);
		systemMap.supers = new ArrayList<Vertex>();
		return systemMap;
	}

	public void buildMetaAttributeAndSystemMap() {
		Vertex systemMap = buildSystemMap();
		Vertex root = getRoot();
		systemMap.plug();
		((Root) root).systemCache.put(SystemMap.class, systemMap);
		systemMap.enablePropertyConstraint();
	}

	@Override
	public Vertex buildInstance() {
		return new Vertex();
	}

	@Override
	public Snapshot<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Snapshot<Vertex> getInheritings() {
		return inheritings;
	}

	@Override
	public Snapshot<Vertex> getComposites() {
		return () -> metaComposites.stream().map(entry -> entry.getValue().stream()).flatMap(x -> x).iterator();
	}

	@Override
	public Snapshot<Vertex> getMetaComposites(Vertex meta) {
		return () -> {
			for (DependenciesEntry<Vertex> entry : metaComposites)
				if (meta.equals(entry.getKey()))
					return entry.getValue().iterator();
			return Collections.emptyIterator();
		};
	};

	@Override
	public Snapshot<Vertex> getSuperComposites(Vertex superVertex) {
		return () -> {
			for (DependenciesEntry<Vertex> entry : superComposites)
				if (superVertex.equals(entry.getKey()))
					return entry.getValue().iterator();
			return Collections.emptyIterator();
		};
	}

	private Vertex indexByMeta(Vertex meta, Vertex composite) {
		return index(metaComposites, meta, composite);
	}

	private Vertex indexBySuper(Vertex superVertex, Vertex composite) {
		return index(superComposites, superVertex, composite);
	}

	private static Vertex index(Dependencies<DependenciesEntry<Vertex>> multimap, Vertex index, Vertex composite) {
		for (DependenciesEntry<Vertex> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().set(composite);

		Dependencies<Vertex> dependencies = new DependenciesImpl<Vertex>();
		Vertex result = dependencies.set(composite);
		multimap.set(new DependenciesEntry<Vertex>(index, dependencies));
		return result;
	}

	private static boolean unIndex(Dependencies<DependenciesEntry<Vertex>> multimap, Vertex index, Vertex composite) {
		for (DependenciesEntry<Vertex> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().remove(composite);
		return false;
	}

	private boolean unIndexByMeta(Vertex meta, Vertex composite) {
		return unIndex(metaComposites, meta, composite);
	}

	private boolean unIndexBySuper(Vertex superVertex, Vertex composite) {
		return unIndex(superComposites, superVertex, composite);
	}

	private static Vertex index(Dependencies<Vertex> dependencies, Vertex dependency) {
		return dependencies.set(dependency);
	}

	private static boolean unIndex(Dependencies<Vertex> dependencies, Vertex dependency) {
		return dependencies.remove(dependency);
	}

	private Vertex indexInstance(Vertex instance) {
		return index(instances, instance);
	}

	private Vertex indexInheriting(Vertex inheriting) {
		return index(inheritings, inheriting);
	}

	private boolean unIndexInstance(Vertex instance) {
		return unIndex(instances, instance);
	}

	private boolean unIndexInheriting(Vertex inheriting) {
		return unIndex(inheritings, inheriting);
	}

	@Override
	public Vertex plug() {
		Vertex t = getMeta().indexInstance(this);
		getSupersStream().forEach(superGeneric -> superGeneric.indexInheriting(this));
		getComponentsStream().forEach(component -> component.indexByMeta(getMeta(), this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.indexBySuper(superGeneric, this)));
		return t;
	}

	@Override
	public boolean unplug() {
		boolean result = getMeta().unIndexInstance(this);
		if (!result)
			rollbackAndThrowException(new NotFoundException(this.info()));
		getSupersStream().forEach(superGeneric -> superGeneric.unIndexInheriting(this));
		getComponentsStream().forEach(component -> component.unIndexByMeta(getMeta(), this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.unIndexBySuper(superGeneric, this)));
		return result;
	}

}
