package org.genericsystem.cache;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Checker;

public class Transaction<T extends AbstractGeneric<T>> extends Context<T> {

	protected Transaction(DefaultEngine<T> engine) {
		super(engine);
	}

	private static class TransactionChecker<T extends AbstractGeneric<T>> extends Checker<T> {
		private TransactionChecker(Transaction<T> transaction) {
			super(transaction);
		}

		@Override
		public void checkAfterBuild(boolean isOnAdd, boolean isFlushTime, T vertex) throws RollbackException {
			checkSystemConstraintsAfterBuild(isOnAdd, isFlushTime, vertex);// Check only system constraints on transactions
		}
	}

	@Override
	protected Checker<T> buildChecker() {
		return new TransactionChecker<T>(this);
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		super.apply(adds, removes);
	}

	@Override
	protected T plug(T generic) {
		return super.plug(generic);
	}

	@Override
	protected boolean unplug(T generic) {
		return super.unplug(generic);
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return super.getRoot();
	}

}
