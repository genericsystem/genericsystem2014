//package org.genericsystem.concurrency;
//
//import org.genericsystem.cache.AbstractGeneric;
//import org.genericsystem.concurrency.Cache.ContextEventListener;
//
//public interface DefaultEngine<T extends AbstractGeneric<T>> extends org.genericsystem.cache.DefaultEngine<T> {
//
//	@Override
//	default Cache<T> newCache() {
//		return new Cache<>(new Transaction<>((DefaultEngine<T>) getRoot()));
//	}
//
//	default Cache<T> newCache(ContextEventListener<T> listener) {
//		return new Cache<>(new Transaction<>((DefaultEngine<T>) getRoot()), listener);
//	}
//
//	@Override
//	public Cache<T> getCurrentCache();
// }
