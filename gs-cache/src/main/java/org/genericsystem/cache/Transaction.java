package org.genericsystem.cache;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.DefaultContext;

public class Transaction<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> implements DefaultContext<T> {

	private transient final DefaultEngine<T, V> engine;
	private final V root;

	protected final TransactionCache<T, V> vertices;

	private final Context<V> context;

	@SuppressWarnings("unchecked")
	protected Transaction(DefaultEngine<T, V> engine) {
		this.engine = engine;
		vertices = new TransactionCache<>(engine);
		root = unwrap((T) engine);
		context = unwrap((T) engine).getCurrentCache();
	}

	@Override
	public boolean isAlive(T generic) {
		AbstractVertex<?> vertex = unwrap(generic);
		return vertex != null && vertex.isAlive();
	}

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
	}

	@SuppressWarnings("unchecked")
	protected T plug(T generic) {
		V meta = unwrap(generic.getMeta());
		List<V> supers = generic.getSupers().stream().map(this::unwrap).collect(Collectors.toList());
		List<V> components = generic.getComponents().stream().map(this::unwrap).collect(Collectors.toList());
		if (meta == null) {
			V adjustedMeta = root.adjustMeta(components.size());
			vertices.put(generic, adjustedMeta.getComponents().size() == components.size() ? adjustedMeta : root.newT(null, meta, supers, generic.getValue(), components).plug());
		} else {
			V instance = meta.getDirectInstance(generic.getValue(), components);
			vertices.put(generic, instance != null ? instance : unwrap((T) engine).newT(null, meta, supers, generic.getValue(), components).plug());
		}
		return generic;
	}

	// TODO remove should return a boolean.
	protected boolean unplug(T generic) {
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
		return generic == null ? null : vertices.get(generic);
	}

	private T wrap(V vertex) {
		return vertices.getByValue(vertex);
	}
}
