//package org.genericsystem.concurrency;
//
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.genericsystem.cache.AbstractGeneric;
//import org.genericsystem.cache.Generic;
//import org.genericsystem.cache.Generic.SystemClass;
//import org.genericsystem.kernel.Builder;
//
//public class Archiver<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver<T> {
//
//	public Archiver(DefaultEngine<T> engine, String directoryPath) {
//		super(engine, directoryPath);
//	}
//
//	@Override
//	protected Saver getSaver(ObjectOutputStream objectOutputStream, long ts) {
//		return new Saver(objectOutputStream, ts);
//	}
//
//	@Override
//	protected Loader getLoader(ObjectInputStream objectInputStream) {
//
//		return new Loader(objectInputStream) {
//			@Override
//			protected org.genericsystem.cache.Transaction<T> buildTransaction() {
//				return new org.genericsystem.cache.Transaction<T>((DefaultEngine<T>) root, ((DefaultEngine<T>) root).pickNewTs()) {
//
//					@Override
//					protected Builder<T> buildBuilder() {
//						return new Builder<T>(this) {
//							@Override
//							@SuppressWarnings("unchecked")
//							protected Class<T> getTClass() {
//								return (Class<T>) Generic.class;
//							}
//
//							@Override
//							@SuppressWarnings("unchecked")
//							protected Class<T> getSystemTClass() {
//								return (Class<T>) SystemClass.class;
//							}
//						};
//					}
//				};
//			}
//		};
//	}
//
//	protected class Saver extends org.genericsystem.kernel.Archiver<T>.Saver {
//		protected Saver(ObjectOutputStream outputStream, long ts) {
//			super(outputStream, ts);
//		}
//
//		@Override
//		protected Transaction<T> buildTransaction(long ts) {
//			return new Transaction<T>((DefaultEngine<T>) root, ts);
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		protected List<T> getOrderedVertices() {
//			return new ArrayList<>(getTransaction().computeDependencies((T) root));
//		}
//	}
// }
