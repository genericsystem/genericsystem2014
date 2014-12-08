package org.genericsystem.concurrency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class Archiver<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver<T> {

	public Archiver(DefaultEngine<T> engine, String directoryPath) {
		this(engine, new ZipFileManager(), directoryPath);
	}

	public Archiver(DefaultEngine<T> engine, FileManager fileManager, String directoryPath) {
		super(new WriterLoaderManager<>(new Transaction<>(engine), fileManager), directoryPath);
	}

	@Override
	public Archiver<T> startScheduler() {
		return (Archiver<T>) super.startScheduler();
	}

	public static class WriterLoaderManager<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver.WriterLoaderManager<T> {

		public WriterLoaderManager(Transaction<T> transaction, FileManager fileManager) {
			super(transaction, fileManager);
		}

		@Override
		protected List<T> getOrderedVertices() {
			return new ArrayList<>(new DependenciesOrder<T>(transaction.getTs()).visit((T) transaction.getRoot()).descendingSet());
		}

		public static class DependenciesOrder<T extends AbstractGeneric<T>> extends TreeSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;

			private final long ts;

			public DependenciesOrder(long ts) {
				this.ts = ts;
			}

			public DependenciesOrder<T> visit(T node) {
				if (!contains(node)) {
					Iterator<T> iterator = node.getCompositesDependencies().iterator(ts);
					while (iterator.hasNext())
						visit(iterator.next());
					iterator = node.getInheritingsDependencies().iterator(ts);
					while (iterator.hasNext())
						visit(iterator.next());
					iterator = node.getInstancesDependencies().iterator(ts);
					while (iterator.hasNext())
						visit(iterator.next());
					add(node);
				}
				return this;
			}
		}

		@Override
		protected Long[] loadOtherTs() throws IOException {
			return new Long[] { inputStream.readLong(), inputStream.readLong(), inputStream.readLong() };
		}

		@Override
		protected T restoreTs(T dependency, Long designTs, Long[] otherTs) {
			return dependency.restore(designTs, otherTs[0], otherTs[1], otherTs[2]);
		}

		@Override
		protected void writeOtherTs(T dependency) throws IOException {
			outputStream.writeLong(dependency.getLifeManager().getBirthTs());
			outputStream.writeLong(dependency.getLifeManager().getLastReadTs());
			outputStream.writeLong(dependency.getLifeManager().getDeathTs());
		}

		@Override
		protected void writeAncestorId(T ancestor) throws IOException {
			outputStream.writeLong(ancestor.getLifeManager().getDesignTs());
		}

	}
}
