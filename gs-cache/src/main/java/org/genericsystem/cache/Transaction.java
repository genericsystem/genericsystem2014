package org.genericsystem.cache;

import java.util.Collections;
import java.util.stream.Collectors;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends AbstractContext<T, U, V, W> {

	private transient final U engine;
	protected final TransactionCache<T, V> vertices;

	@SuppressWarnings("unchecked")
	protected Transaction(U engine) {
		this.engine = engine;
		vertices = new TransactionCache<>((T) engine);
	}

	@Override
	public boolean isAlive(T generic) {
		AbstractVertex<?, ?> vertex = generic.unwrap();
		return vertex != null && vertex.isAlive();
	}

	@Override
	protected void simpleAdd(T generic) {
		V vertex = unwrap(generic.getMeta());
		// TODO null is KK
		V result = vertex.bindInstance(null, generic.isThrowExistException(), generic.getSupersStream().map(this::unwrap).collect(Collectors.toList()), generic.getValue(), generic.getComponentsStream().map(this::unwrap).collect(Collectors.toList()));
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
