package org.genericsystem.concurrency.generic;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.cache.CacheDependencies;
import org.genericsystem.impl.GenericSignature;

public class GenericConcurrency extends GenericSignature<GenericConcurrency> implements GenericServiceConcurrency<GenericConcurrency, EngineConcurrency> {

	@Override
	public GenericConcurrency buildInstance() {
		return new GenericConcurrency();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends GenericConcurrency> CacheDependencies<U> buildDependencies(Supplier<Iterator<GenericConcurrency>> subDependenciesSupplier) {
		return (CacheDependencies<U>) new CacheDependencies<GenericConcurrency>(subDependenciesSupplier);
	}
}
