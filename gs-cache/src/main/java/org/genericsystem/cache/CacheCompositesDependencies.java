//package org.genericsystem.cache;
//
//import java.util.Iterator;
//import java.util.function.Supplier;
//import org.genericsystem.kernel.Dependencies;
//import org.genericsystem.kernel.Dependencies.CompositesDependencies;
//import org.genericsystem.kernel.Dependencies.DependenciesEntry;
//
//public abstract class CacheCompositesDependencies<T> extends CacheDependencies<DependenciesEntry<T>> implements CompositesDependencies<T> {
//
//	public CacheCompositesDependencies(Supplier<Iterator<DependenciesEntry<T>>> iteratorSupplier) {
//		super(iteratorSupplier);
//	}
//
//	@Override
//	public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
//		assert false;
//		return new CacheDependencies<T>(supplier);
//	}
// }
