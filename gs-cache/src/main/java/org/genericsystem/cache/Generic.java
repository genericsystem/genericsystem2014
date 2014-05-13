package org.genericsystem.cache;

import java.io.Serializable;
import java.util.stream.Stream;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;

public class Generic extends AbstractVertex<Generic> implements GenericService<Generic> {

	public Generic(Generic meta, Generic[] supers, Serializable value, Generic[] components) {
		super(meta, supers, value, components);
	}

	@Override
	public Generic build(Generic meta, Stream<Generic> overrides, Serializable value, Stream<Generic> components) {
		return new Generic(meta, overrides.toArray(Generic[]::new), value, components.toArray(Generic[]::new));
	}

	@Override
	public Generic[] getEmptyArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getInstance(Serializable value, Generic... components) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dependencies<Generic> getInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dependencies<Generic> getInheritings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompositesDependencies<Generic> getMetaComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompositesDependencies<Generic> getSuperComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<Generic> getInheritings(Generic origin, int level) {
		// TODO Auto-generated method stub
		return null;
	}

}
