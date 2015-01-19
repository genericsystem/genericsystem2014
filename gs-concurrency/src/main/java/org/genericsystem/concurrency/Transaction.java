package org.genericsystem.concurrency;

import org.genericsystem.cache.Generic;

public class Transaction extends org.genericsystem.cache.Transaction<Generic> {

	Transaction(Engine engine) {
		super(engine);
	}

	protected Transaction(Engine engine, long ts) {
		super(engine, ts);
	}

	@Override
	protected Generic plug(Generic generic) {
		// generic.getLifeManager().beginLife(getTs());
		return super.plug(generic);
	}

	@Override
	protected void unplug(Generic generic) {
		generic.getLifeManager().kill(getTs());
		((Engine) getRoot()).getGarbageCollector().add(generic);
	}
}
