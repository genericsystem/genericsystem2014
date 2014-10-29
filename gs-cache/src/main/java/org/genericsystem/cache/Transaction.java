package org.genericsystem.cache;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class Transaction<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends AbstractContext<T, V> {

	private transient final DefaultEngine<T, V> engine;
	protected final TransactionCache<T, V> vertices;

	protected Transaction(DefaultEngine<T, V> engine) {
		this.engine = engine;
		vertices = new TransactionCache<>(engine);
	}

	@Override
	public boolean isAlive(T generic) {
		AbstractVertex<?> vertex = generic.unwrap();
		return vertex != null && vertex.isAlive();
	}

	@Override
	protected void simpleAdd(T generic) {
		V vertex = unwrap(generic.getMeta());
		// TODO null is KK
		V result = vertex.bindInstance(null, generic.isThrowExistException(), generic.getSupers().stream().map(this::unwrap).collect(Collectors.toList()), generic.getValue(), generic.getComponents().stream().map(this::unwrap).collect(Collectors.toList()));
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
	public DefaultEngine<T, V> getEngine() {
		return engine;
	}

	@Override
	Snapshot<T> getInheritings(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? vertex.getInheritings().get().map(generic::wrap) : Stream.empty();
		};
	}

	@Override
	Snapshot<T> getInstances(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? vertex.getInstances().get().map(generic::wrap) : Stream.empty();
		};
	}

	@Override
	Snapshot<T> getCompositesByMeta(T generic, T meta) {
		return () -> {
			V genericVertex = unwrap(generic);
			V metaVertex = unwrap(meta);
			return genericVertex != null && metaVertex != null ? genericVertex.getCompositesByMeta(metaVertex).get().map(generic::wrap) : Stream.empty();
		};
	}

	@Override
	Snapshot<T> getCompositesBySuper(T generic, T superT) {
		return () -> {
			V genericVertex = unwrap(generic);
			V superVertex = unwrap(superT);
			return genericVertex != null && superVertex != null ? genericVertex.getCompositesBySuper(superVertex).get().map(generic::wrap) : Stream.empty();
		};
	}

	@Override
	protected V unwrap(T generic) {
		return vertices.get(generic);
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		super.apply(adds, removes);
	}

	@Override
	T wrap(V vertex) {
		return vertices.getByValue(vertex);
	}
}
