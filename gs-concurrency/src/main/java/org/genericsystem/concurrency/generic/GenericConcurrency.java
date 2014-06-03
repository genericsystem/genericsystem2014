package org.genericsystem.concurrency.generic;

import org.genericsystem.impl.GenericSignature;

public class GenericConcurrency extends GenericSignature<GenericConcurrency> implements GenericServiceConcurrency<GenericConcurrency> {

	@Override
	public GenericConcurrency buildInstance() {
		return new GenericConcurrency();
	}
}
