package org.genericsystem.cache;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;

public class Transaction<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> implements DefaultContext<T> {

	private transient final DefaultEngine<T, V> engine;
	protected final TransactionCache<T, V> vertices;

	private final Context<V> context;

	protected Transaction(DefaultEngine<T, V> engine) {
		this.engine = engine;
		vertices = new TransactionCache<>(engine);
		context = unwrap((T) engine).getCurrentCache();
	}

	@Override
	public boolean isAlive(T generic) {
		AbstractVertex<?> vertex = unwrap(generic);
		return vertex != null && vertex.isAlive();
	}

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::simpleRemove);
		adds.forEach(this::simpleAdd);
	}

	private T simpleAdd(T generic) {
		V vertex = unwrap(generic.getMeta());
		// TODO null is KK
		V result = null;
		if (vertex == null)
			vertex = unwrap((T) engine);
		result = vertex.setInstance(generic.getSupers().stream().map(this::unwrap).collect(Collectors.toList()), generic.getValue(), vertex.coerceToTArray(generic.getComponents().stream().map(x -> x == null ? x : unwrap(x)).toArray()));
		vertices.put(generic, result);
		return generic;
	}

	// TODO remove should return a boolean.
	protected boolean simpleRemove(T generic) {
		unwrap(generic).remove();
		vertices.put(generic, null);
		return true;
	}

	@Override
	public DefaultEngine<T, V> getRoot() {
		return engine;
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? context.getInheritings(vertex).get().map(this::wrap) : Stream.empty();
		};
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? context.getInstances(vertex).get().map(this::wrap) : Stream.empty();
		};
	}

	@Override
	public Snapshot<T> getComposites(T generic) {
		return () -> {
			V vertex = unwrap(generic);
			return vertex != null ? context.getComposites(vertex).get().map(this::wrap) : Stream.empty();
		};
	}

	protected V unwrap(T generic) {
		return vertices.get(generic);
	}

	private T wrap(V vertex) {
		return vertices.getByValue(vertex);
	}
}
