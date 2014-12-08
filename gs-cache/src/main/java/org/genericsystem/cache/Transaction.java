package org.genericsystem.cache;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
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

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::unplug);
		adds.forEach(this::plug);
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
