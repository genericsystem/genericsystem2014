package org.genericsystem.concurrency;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.genericsystem.kernel.Builder;


public class Archiver<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.Archiver<T> {

	public Archiver(DefaultEngine<T> engine, String directoryPath) {
		super(engine,directoryPath);
	}

	@Override
	protected Saver getSaver(ObjectOutputStream objectOutputStream, long ts) {
		return new Saver(objectOutputStream, ts);
	}
	
	@Override
	protected Loader getLoader(ObjectInputStream objectInputStream) {
		return new Loader(objectInputStream);
	}
	
	protected long pickTs(){
		return ((DefaultEngine<T>) root).pickNewTs();
	}
	
	protected class Saver extends org.genericsystem.kernel.Archiver<T>.Saver {
		protected Saver(ObjectOutputStream outputStream,long ts) {
			super(outputStream, ts);
		}
		@SuppressWarnings("unchecked")
		@Override
		protected List<T> getOrderedVertices() {
			return new ArrayList<>(new DependenciesOrder<T>(ts).visit((T)root));
		}
		
		//TODO remove this
		
		@Override
		protected void writeOtherTs(T dependency) throws IOException {
			objectOutputStream.writeLong(dependency.getLifeManager().getBirthTs());
			objectOutputStream.writeLong(dependency.getLifeManager().getLastReadTs());
			objectOutputStream.writeLong(dependency.getLifeManager().getDeathTs());
		}

		@Override
		protected void writeAncestorId(T ancestor) throws IOException {
			objectOutputStream.writeLong(ancestor.getLifeManager().getDesignTs());
		}
	}
	
	protected static class DependenciesOrder<T extends AbstractGeneric<T>> extends TreeSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;

		private final long ts;

		DependenciesOrder(long ts) {
			this.ts = ts;
		}

		DependenciesOrder<T> visit(T node) {
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
	
	
	protected class Loader extends org.genericsystem.kernel.Archiver<T>.Loader {
		protected Loader(ObjectInputStream objectInputStream) {
			super(objectInputStream);
		}
		
		protected Transaction<T> buildTransaction(){
			return new Transaction<T>((DefaultEngine<T>)root){
				@Override
				public T plug(T generic) {
					return simplePlug(generic);
				}
				
				@Override
				protected Builder<T> buildBuilder() {
					return new Builder<T>(this) {
						@Override
						@SuppressWarnings("unchecked")
						protected Class<T> getTClass() {
							return (Class<T>) Generic.class;
						}
					};
				}
				
				//TODO checker
			};
		}
		
		@Override
		protected Long[] loadOtherTs() throws IOException {
			return new Long[] { objectInputStream.readLong(), objectInputStream.readLong(), objectInputStream.readLong() };
		}

		@Override
		protected T restoreTs(T dependency, Long designTs, Long[] otherTs) {
			return dependency.restore(designTs, otherTs[0], otherTs[1], otherTs[2]);
		}
	}
}
