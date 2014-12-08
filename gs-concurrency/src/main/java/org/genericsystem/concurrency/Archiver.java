package org.genericsystem.concurrency;

import java.io.IOException;
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
		protected List<T> getOrderedVertices() {
			return new ArrayList<>(new DependenciesOrder<T>(ts).visit(root));
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
					if (!node.isRoot())
						add(node);
				}
				return this;
			}
		}
		
		@Override
		protected Long[] loadOtherTs() throws IOException{
			return new Long[]{inputStream.readLong(),inputStream.readLong(),inputStream.readLong()};
		}

		@SuppressWarnings("unchecked")
		@Override
		protected long pickNewTs() {
			return ((DefaultEngine<T>) root).pickNewTs();
		}
		
		protected T restoreTs(T dependency,Long designTs,Long[] otherTs){
			return dependency.restore(designTs, otherTs[0], otherTs[1], otherTs[2]);
		}

		@Override
		protected void writeOtherTs(T dependency) throws IOException{
			outputStream.writeLong(dependency.getLifeManager().getBirthTs());
			outputStream.writeLong( dependency.getLifeManager().getLastReadTs());
			outputStream.writeLong( dependency.getLifeManager().getDeathTs());
		}
		
		protected void writeAncestorId(T ancestor) throws IOException {
			outputStream.writeLong(ancestor.getLifeManager().getDesignTs());
		}

	}
}
