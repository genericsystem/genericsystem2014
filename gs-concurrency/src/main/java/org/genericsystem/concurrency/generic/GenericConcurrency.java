package org.genericsystem.concurrency.generic;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.cache.CacheDependencies;
import org.genericsystem.impl.GenericSignature;

public class GenericConcurrency extends GenericSignature<GenericConcurrency> implements GenericServiceConcurrency<GenericConcurrency> {

	@Override
	public GenericConcurrency buildInstance() {
		return new GenericConcurrency();
	}

	@Override
	public CacheDependencies<GenericConcurrency> buildDependencies(Supplier<Iterator<GenericConcurrency>> subDependenciesSupplier) {
		return new CacheDependencies<GenericConcurrency>(subDependenciesSupplier);
	}
}
