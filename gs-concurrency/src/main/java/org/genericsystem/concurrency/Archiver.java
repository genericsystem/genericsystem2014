package org.genericsystem.concurrency;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.genericsystem.concurrency.Generic.SystemClass;
import org.genericsystem.kernel.Builder;

public class Archiver<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver<T> {

	public Archiver(DefaultEngine<T> engine, String directoryPath) {
		super(engine, directoryPath);
	}

	@Override
	protected Saver getSaver(ObjectOutputStream objectOutputStream, long ts) {
		return new Saver(objectOutputStream, ts);
	}

	@Override
	protected Loader getLoader(ObjectInputStream objectInputStream) {
		return new Loader(objectInputStream);
	}

	@Override
	protected long pickTs() {
		return ((DefaultEngine<T>) root).pickNewTs();
	}

	protected class Saver extends org.genericsystem.kernel.Archiver<T>.Saver {
		protected Saver(ObjectOutputStream outputStream, long ts) {
			super(outputStream, ts);
		}

		@Override
		protected org.genericsystem.kernel.Transaction<T> buildTransaction(long ts) {
			return new Transaction<>((DefaultEngine<T>) root, ts);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<T> getOrderedVertices() {
			return new ArrayList<>(getTransaction().computeDependencies((T) root));
		}

		@Override
		protected void writeAncestorId(T ancestor) throws IOException {
			objectOutputStream.writeLong(ancestor != null ? ancestor.getTs() : -1L);
		}
	}

	protected class Loader extends org.genericsystem.kernel.Archiver<T>.Loader {
		protected Loader(ObjectInputStream objectInputStream) {
			super(objectInputStream);
		}

		@Override
		protected org.genericsystem.cache.Transaction<T> buildTransaction() {
			return new TsTransaction();
		}

		private class TsTransaction extends org.genericsystem.cache.Transaction<T> {
			TsTransaction() {
				super((DefaultEngine<T>) root, ((DefaultEngine<T>) root).pickNewTs());
			}

			@Override
			protected Builder<T> buildBuilder() {
				return new TsBuilder();
			}

			private class TsBuilder extends AbstractVertexBuilder<T> {
				protected TsBuilder() {
					super(TsTransaction.this);
				}

				@Override
				@SuppressWarnings("unchecked")
				protected Class<T> getTClass() {
					return (Class<T>) Generic.class;
				}

				@Override
				@SuppressWarnings("unchecked")
				protected Class<T> getSystemTClass() {
					return (Class<T>) SystemClass.class;
				}
			}
		}
	}
}
