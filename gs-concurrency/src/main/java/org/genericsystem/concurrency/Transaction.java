package org.genericsystem.concurrency;

import java.util.Collections;
import org.genericsystem.concurrency.vertex.Root;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.services.RootService;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Transaction<T, U, V, W> implements Context<T, U, V, W> {

	private transient long ts;

	// private ExecutorService service = Executors.newSingleThreadExecutor(r -> new GsThread(r, getTs()));

	// private <R> R callWithTs(Callable<R> callable) {
	// try {
	// return service.submit(callable).get();
	// } catch (InterruptedException | ExecutionException e) {
	// getEngine().rollbackAndThrowException(e);
	// return null;
	// }
	// }

	public Transaction(U engine) {
		this(((Root) ((Engine) engine).getVertex()).pickNewTs(), engine);
	}

	public Transaction(long ts, U engine) {
		super(engine);
		this.ts = ts;
	}

	@Override
	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getVertex() != null && generic.getLifeManager().isAlive(getTs());
	}

	// TODO Apply with checkMvcc...

	@Override
	public void simpleAdd(T generic) {
		// TODO Here we have to begin generic's life with ts;
		super.simpleAdd(generic);
	}

	@Override
	public boolean simpleRemove(T generic) {
		// TODO Here we have to kill generic with ts...
		return super.simpleRemove(generic);
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInheritings().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInstances().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T generic, T meta) {
		return () -> generic.getVertex() != null ? generic.unwrap().getMetaComposites(meta.unwrap()).stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getSuperComposites(T generic, T superT) {
		return () -> generic.getVertex() != null ? generic.unwrap().getSuperComposites(superT.unwrap()).stream().map(generic::wrap).iterator() : Collections.emptyIterator();

	};
}
