package org.genericsystem.cache;

import java.io.Serializable;
import java.util.stream.Stream;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;

public class Generic<T extends Generic<T>> extends AbstractVertex<T> implements GenericService<T> {

	public Generic(T meta, T[] supers, Serializable value, T... components) {
		super(meta, supers, value, components);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T build(T meta, Stream<T> overrides, Serializable value, Stream<T> components) {
		return (T) new GenericImpl((GenericImpl) meta, overrides.toArray(GenericImpl[]::new), value, components.toArray(GenericImpl[]::new));
	}

	@Override
	public T[] getEmptyArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getInstance(Serializable value, Generic... components) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dependencies<T> getInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dependencies<T> getInheritings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompositesDependencies<T> getMetaComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompositesDependencies<T> getSuperComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<T> getInheritings(Generic origin, int level) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class GenericImpl extends Generic<GenericImpl> {

		public GenericImpl(GenericImpl meta, GenericImpl[] supers, Serializable value, GenericImpl[] components) {
			super(meta, supers, value, components);
		}

	}

}
