package org.genericsystem.cache;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.impl.GenericSignature;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	@Override
	public CacheDependencies<Generic> buildDependencies(Supplier<Iterator<Generic>> subDependenciesSupplier) {
		return new CacheDependencies<Generic>(subDependenciesSupplier);
	}

}
