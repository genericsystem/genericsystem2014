package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;

public class Transaction<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends AbstractContext<T, V> {

	private transient final DefaultEngine<T, V> engine;

	protected final TransactionCache<T, V> vertices;

	protected Transaction(DefaultEngine<T, V> engine) {
		this.engine = engine;
		vertices = new TransactionCache<>(engine);
	}

	@Override
	protected T getOrBuildT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return vertices.getOrBuildT(clazz, meta, supers, value, components);
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
		V result = null;
		if (vertex == null)
			vertex = unwrap((T) engine);
		result = vertex.setInstance(generic.getSupers().stream().map(this::unwrap).collect(Collectors.toList()), generic.getValue(), vertex.coerceToTArray(generic.getComponents().stream().map(this::unwrap).toArray()));

		vertices.put(generic, result);// ***
	}

	// remove should return a boolean.
	@Override
	protected boolean simpleRemove(T generic) {
		unwrap(generic).remove();
		vertices.put(generic, null);// ***
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
	Snapshot<T> getComposites(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? vertex.getComposites().get().map(generic::wrap) : Stream.empty();
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
