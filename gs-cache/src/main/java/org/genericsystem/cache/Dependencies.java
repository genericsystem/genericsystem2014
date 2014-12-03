package org.genericsystem.cache;

import java.util.Iterator;
import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.kernel.TimestampDependencies;

public interface Dependencies<T> extends IteratorSnapshot<T>, TimestampDependencies<T> {

	@Override
	default Iterator<T> iterator(long ts) {
		return iterator();
	}

}
