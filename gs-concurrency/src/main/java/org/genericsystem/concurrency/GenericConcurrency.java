package org.genericsystem.concurrency;

import java.io.Serializable;

import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public class GenericConcurrency extends GenericSignature<GenericConcurrency> implements GenericServiceConcurrency<GenericConcurrency> {

	public boolean isAlive(long ts) {
		return unwrap().isAlive(ts);
	}

	@Override
	public GenericConcurrency wrap(Vertex vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VertexConcurrency unwrap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

	@Override
	public GenericConcurrency getInstance(Serializable value, GenericConcurrency... components) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dependencies<GenericConcurrency> getInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dependencies<GenericConcurrency> getInheritings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompositesDependencies<GenericConcurrency> getMetaComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompositesDependencies<GenericConcurrency> getSuperComposites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericConcurrency buildInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<GenericConcurrency> getMetaComposites(GenericConcurrency meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Snapshot<GenericConcurrency> getSuperComposites(GenericConcurrency superVertex) {
		// TODO Auto-generated method stub
		return null;
	}

}
