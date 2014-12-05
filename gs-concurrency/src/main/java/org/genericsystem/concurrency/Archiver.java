package org.genericsystem.concurrency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class Archiver<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver<T> {

	public Archiver(T root, String directoryPath) {
		this(root, new ZipFileManager(), directoryPath);
	}

	public Archiver(T root, FileManager fileManager, String directoryPath) {
		super(new WriterLoaderManager<>(root, fileManager), directoryPath);
	}

	@Override
	public Archiver<T> startScheduler() {
		return (Archiver<T>) super.startScheduler();
	}

	public static class WriterLoaderManager<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver.WriterLoaderManager<T> {

		public WriterLoaderManager(T root, FileManager fileManager) {
			super(root, fileManager);
		}

		@Override
		protected List<T> getOrderedVertex() {
			return new ArrayList<>(new DependenciesOrder<T>().visit(root));
		}

		public static class DependenciesOrder<T extends AbstractGeneric<T>> extends TreeSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;

			public DependenciesOrder<T> visit(T node) {
				if (!contains(node)) {
					// TODO call WriterLoaderManager::getTs(T dependency)
					long ts = node.getLifeManager().getDesignTs();
					Iterator<T> iterator = node.getCompositesDependencies().iterator(ts);
					while (iterator.hasNext())
						visit(iterator.next());
					iterator = node.getInheritingsDependencies().iterator(ts);
					while (iterator.hasNext())
						visit(iterator.next());
					iterator = node.getInstancesDependencies().iterator(ts);
					while (iterator.hasNext())
						visit(iterator.next());
					if (!node.isRoot())
						add(node);
				}
				return this;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected long pickNewTs() {
			return ((DefaultEngine<T>) root).pickNewTs();
		}

		@Override
		protected long getTs(T dependency) {
			return dependency.getLifeManager().getDesignTs();
		}

	}
}
