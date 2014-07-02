package org.genericsystem.cache;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Vertex;

public class Transaction<T extends GenericService<T>> implements Context<T> {

	private transient final EngineService<T> engine;

	public Transaction(EngineService<T> engine) {
		this.engine = engine;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getVertex() != null && generic.getVertex().isAlive();
	}

	@Override
	public void simpleAdd(T generic) {
		generic.getMeta().getVertex().addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), generic.getComponentsStream().map(g -> g.unwrap()).toArray(Vertex[]::new));
	}

	@Override
	public void simpleRemove(T generic) {
		// TODO Auto-generated method stub
	}

	@Override
	public EngineService<T> getEngine() {
		return engine;
	}

	@Override
	public Dependencies<T> getInheritings(T generic) {
		return new Dependencies<T>() {

			@Override
			public Iterator<T> iterator() {
				return generic.unwrap().getInheritings().project(generic::wrap).iterator();
			}

			@Override
			public boolean remove(T vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(T vertex) {
				assert false;
			}

		};
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		return new Dependencies<T>() {

			@Override
			public Iterator<T> iterator() {
				return generic.unwrap().getInstances().project(generic::wrap).iterator();
			}

			@Override
			public boolean remove(T vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(T vertex) {
				assert false;
			}

		};
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return new CompositesDependencies<T>() {

			@Override
			public boolean remove(DependenciesEntry<T> vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(DependenciesEntry<T> vertex) {
				assert false;
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return generic.unwrap().getMetaComposites().stream().map(x -> buildEntry(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator();
			}

			@Override
			public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
				assert false;
				return null;
			}

		};
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return new CompositesDependencies<T>() {

			@Override
			public boolean remove(DependenciesEntry<T> vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(DependenciesEntry<T> vertex) {
				assert false;
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return generic.unwrap().getSuperComposites().stream().map(x -> buildEntry(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator();
			}

			@Override
			public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
				assert false;
				return null;
			}

		};
	};

}
