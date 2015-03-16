package org.genericsystem.cache;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Generic;

public class Transaction extends org.genericsystem.kernel.Transaction {

	protected Transaction(Engine engine, long ts) {
		super(engine, ts);
	}

	protected Transaction(Engine engine) {
		super(engine);
	}

	@Override
	protected void unplug(Generic generic) {
		generic.getLifeManager().kill(getTs());
		((DefaultEngine) getRoot()).getGarbageCollector().add(generic);
	}

	@Override
	protected Checker buildChecker() {
		return new Checker(Transaction.this) {
			@Override
			public void checkAfterBuild(boolean isOnAdd, boolean isFlushTime, Generic vertex) throws RollbackException {
				checkSystemConstraintsAfterBuild(isOnAdd, isFlushTime, vertex);// Check only system constraints on transactions
			}
		};
	}
}
