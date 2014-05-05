package org.genericsystem.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.genericsystem.cache.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.iterator.AbstractAwareIterator;

public class CacheImpl extends AbstractContext implements Cache {

	private AbstractContext subContext;

	private Map<Vertex, LifeManager> map = new HashMap<>();

	private transient Map<Vertex, TimestampedDependencies> inheritingAndInstancesDependenciesMap;
	private transient Map<Vertex, TimestampedDependencies> compositeDependenciesMap;

	public CacheImpl(Cache cache) {
		subContext = (CacheImpl) cache;
		clear();
	}

	public CacheImpl(CacheRoot root) {
		subContext = new Transaction(root);
		clear();
	}

	public void clear() {
		inheritingAndInstancesDependenciesMap = new HashMap<>();
		compositeDependenciesMap = new HashMap<>();
	}

	<T extends Vertex> T insert(T vertex) throws RollbackException {
		try {
			LifeManager lifeManager = getLifeManager(vertex);
			if (lifeManager == null || getTs() > lifeManager.getDeathTs()) {
				addVertex(vertex);
				return vertex;
			}
		} catch (ConstraintViolationException e) {
			// rollback(e);
		}
		throw new IllegalStateException();// Unreachable;
	}

	private void addVertex(Vertex vertex) throws ConstraintViolationException {
		map.put(vertex, new LifeManager(getTs()));
		simpleAdd(vertex);
		// check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	private void removeVertex(Vertex vertex) throws ConstraintViolationException {
		LifeManager lifeManager = getLifeManager(vertex);
		lifeManager.setDeathTs(getTs());
		map.put(vertex, lifeManager);
		simpleRemove(vertex);
		// check(CheckingType.CHECK_ON_ADD_NODE, false, generic);
	}

	@Override
	public void addInstance(Vertex meta, Serializable value, Stream<Vertex> components) {
		// insert(meta.addInstance(value, components));
	}

	@Override
	public Stream<Vertex> getInstances(Vertex vertex) {
		LifeManager lifeManager = getLifeManager(vertex);
		if (lifeManager != null && getTs() > lifeManager.getDesignTs() && getTs() < lifeManager.getDeathTs())
			return new AbstractSnapshot<Vertex>() {
				@Override
				public Iterator<Vertex> iterator() {
					return inheritingAndInstancesDependenciesMap.get(vertex).iterator(getTs());
				}
			}.stream();
		return AbstractSnapshot.<Vertex> emptySnapshot().stream();
	}

	@Override
	public LifeManager getLifeManager(Vertex vertex) {
		LifeManager lifeManager = map.get(vertex);
		return lifeManager == null ? subContext.getLifeManager(vertex) : lifeManager;
	}

	@Override
	public <T extends CacheRoot> T getRoot() {
		return subContext.getRoot();
	}

	@Override
	public long getTs() {
		return subContext.getTs();
	}

	@Override
	TimestampedDependencies getInheritings(Vertex vertex) {
		TimestampedDependencies dependencies = inheritingAndInstancesDependenciesMap.get(vertex);
		if (dependencies == null) {
			TimestampedDependencies result = inheritingAndInstancesDependenciesMap.put(vertex, dependencies = new CacheDependencies(subContext.getInheritings(vertex)));
			assert result == null;
		}
		return dependencies;
	}

	@Override
	TimestampedDependencies getComposites(Vertex vertex) {
		TimestampedDependencies dependencies = compositeDependenciesMap.get(vertex);
		if (dependencies == null) {
			TimestampedDependencies result = compositeDependenciesMap.put(vertex, dependencies = new CacheDependencies(subContext.getComposites(vertex)));
			assert result == null;
		}
		return dependencies;
	}

	static class CacheDependencies implements TimestampedDependencies {

		private transient TimestampedDependencies underlyingDependencies;

		private final DependenciesImpl<Vertex> inserts = new DependenciesImpl<>();
		private final DependenciesImpl<Vertex> deletes = new DependenciesImpl<>();

		public CacheDependencies(TimestampedDependencies underlyingDependencies) {
			assert underlyingDependencies != null;
			this.underlyingDependencies = underlyingDependencies;
		}

		@Override
		public void add(Vertex vertex) {
			inserts.add(vertex);
		}

		@Override
		public void remove(Vertex vertex) {
			if (!inserts.remove(vertex))
				deletes.add(vertex);
		}

		@Override
		public Iterator<Vertex> iterator(long ts) {
			return new InternalIterator(underlyingDependencies.iterator(ts));
		}

		private class InternalIterator extends AbstractAwareIterator<Vertex> implements Iterator<Vertex> {
			private final Iterator<Vertex> underlyingIterator;
			private final Iterator<Vertex> insertsIterator = inserts.iterator();

			private InternalIterator(Iterator<Vertex> underlyingIterator) {
				this.underlyingIterator = underlyingIterator;
			}

			@Override
			protected void advance() {
				while (underlyingIterator.hasNext()) {
					Vertex vertex = underlyingIterator.next();
					if (!deletes.contains(vertex)) {
						next = vertex;
						return;
					}
				}
				while (insertsIterator.hasNext()) {
					next = insertsIterator.next();
					return;
				}
				next = null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}

}
