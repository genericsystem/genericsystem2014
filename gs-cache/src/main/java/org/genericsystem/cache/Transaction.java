package org.genericsystem.cache;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Checker;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Transaction<T> {

	protected Transaction(DefaultEngine<T> engine, long ts) {
		super(engine, ts);
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
	protected T plug(T generic) {
		return super.plug(generic);
	}

	@Override
	protected void unplug(T generic) {
		super.unplug(generic);
	}

	@Override
	protected Builder<T> buildBuilder() {
		return new Builder<T>(this) {
			@Override
			protected Class<T> getTClass() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
