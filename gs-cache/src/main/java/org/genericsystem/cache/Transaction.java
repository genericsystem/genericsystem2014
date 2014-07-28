package org.genericsystem.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.services.RootService;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends AbstractContext<T, U, V, W> {

	private transient final U engine;
	protected final VerticesMap<T, V> vertices;

	@SuppressWarnings("unchecked")
	protected Transaction(U engine) {
		this.engine = engine;
		vertices = new VerticesMap<>((T) engine);
	}

	@Override
	public boolean isAlive(T generic) {
		AbstractVertex<?, ?> vertex = generic.unwrap();
		return vertex != null && vertex.isAlive();
	}

	@Override
	protected void simpleAdd(T generic) {
		V vertex = unwrap(generic.getMeta());
		V result = vertex.bindInstance(generic.isThrowExistException(), generic.getSupersStream().map(this::unwrap).collect(Collectors.toList()), generic.getValue(), generic.getComponentsStream().map(this::unwrap).collect(Collectors.toList()));
		vertices.put(generic, result);
	}

	// remove should return a boolean.
	@Override
	protected boolean simpleRemove(T generic) {
		generic.unwrap().remove();
		vertices.put(generic, null);
		return true;
	}

	@Override
	public U getEngine() {
		return engine;
	}

	@Override
	Snapshot<T> getInheritings(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? vertex.getInheritings().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
		};
	}

	@Override
	Snapshot<T> getInstances(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? vertex.getInstances().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
		};
	}

	@Override
	Snapshot<T> getMetaComposites(T generic, T meta) {
		return () -> {
			V genericVertex = unwrap(generic);
			V metaVertex = unwrap(meta);
			return genericVertex != null && metaVertex != null ? genericVertex.getMetaComposites(metaVertex).stream().map(generic::wrap).iterator() : Collections.emptyIterator();
		};
	}

	@Override
	Snapshot<T> getSuperComposites(T generic, T superT) {
		return () -> {
			V genericVertex = unwrap(generic);
			V superVertex = unwrap(superT);
			return genericVertex != null && superVertex != null ? genericVertex.getSuperComposites(superVertex).stream().map(generic::wrap).iterator() : Collections.emptyIterator();
		};
	}

	public static class VerticesMap<T extends AbstractGeneric<T, ?, V, ?>, V extends AbstractVertex<V, ?>> extends HashMap<T, V> {

		private static final long serialVersionUID = -2571113223711253002L;

		public VerticesMap(T engine) {
			assert engine.unwrap() != null;
			put(engine, engine.unwrap());
		}

		@SuppressWarnings("unchecked")
		@Override
		public V get(Object key) {
			T generic = (T) key;
			V result = super.get(generic);
			if (result == null) {
				V pluggedMeta = get(generic.getMeta());
				if (pluggedMeta != null)
					for (V instance : pluggedMeta.getInstances())
						if (generic.equiv(instance)) {
							put(generic, instance);
							return instance;
						}
				put(generic, null);
			}
			return result;
		}
	};

	@Override
	protected V unwrap(T generic) {
		return vertices.get(generic);
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		super.apply(adds, removes);
	}
}
