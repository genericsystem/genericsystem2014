package org.genericsystem.cache;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Generic;

public class Transaction extends org.genericsystem.kernel.Transaction {

	protected Transaction(DefaultEngine engine, long ts) {
		super(engine, ts);
	}

	protected Transaction(DefaultEngine engine) {
		super(engine);
	}

	@Override
	protected void unplug(Generic generic) {
		generic.getLifeManager().kill(getTs());
		((DefaultEngine) getRoot()).getGarbageCollector().add(generic);
	}

	@Override
	protected Checker<Generic> buildChecker() {
		return new Checker<Generic>(Transaction.this) {
			@Override
			public void checkAfterBuild(boolean isOnAdd, boolean isFlushTime, Generic vertex) throws RollbackException {
				checkSystemConstraintsAfterBuild(isOnAdd, isFlushTime, vertex);// Check only system constraints on transactions
			}
		};
	}
}
