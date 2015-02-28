package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Builder;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Generic;

public class Transaction extends org.genericsystem.kernel.Transaction<Generic> {

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

	@Override
	protected Builder<Generic> buildBuilder() {
		return new Builder<Generic>(this) {

			@Override
			protected Generic build(long ts, Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
				return newT(clazz, meta).init(ts, meta, supers, value, components, otherTs);
			}
		};
	}
}
