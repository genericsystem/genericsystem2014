package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Checker;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Transaction<T> {

	protected Transaction(DefaultEngine<T> engine, long ts) {
		super(engine, ts);
	}

	protected Transaction(DefaultEngine<T> engine) {
		super(engine);
	}

	@Override
	protected void unplug(T generic) {
		generic.getLifeManager().kill(getTs());
		((DefaultEngine<T>) getRoot()).getGarbageCollector().add(generic);
	}

	@Override
	protected Checker<T> buildChecker() {
		return new Checker<T>(Transaction.this) {
			@Override
			public void checkAfterBuild(boolean isOnAdd, boolean isFlushTime, T vertex) throws RollbackException {
				checkSystemConstraintsAfterBuild(isOnAdd, isFlushTime, vertex);// Check only system constraints on transactions
			}
		};
	}

	@Override
	protected Builder<T> buildBuilder() {
		return new Builder<T>(this) {
			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getTClass() {
				return (Class<T>) Generic.class;
			}

			@Override
			public T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs) {
				return newT(clazz, meta).init(getContext().getRoot().pickNewTs(), meta, supers, value, components, otherTs);
			}
		};
	}
}
