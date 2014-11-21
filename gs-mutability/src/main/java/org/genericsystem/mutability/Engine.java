package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.IRoot;

public class Engine extends Generic implements IRoot<Generic> {

	private Cache cache;

	public Engine() {
		super(null);
		this.engine = this;
		cache = new Cache(this);
		cache.put(this, new org.genericsystem.concurrency.Engine());
	}

	public Cache getCache() {
		return cache;
	}

	@Override
	public Generic addType(Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).addType(value));
	}

	@Override
	public Generic addType(Generic override, Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).addType(unwrap(override), value));
	}

	@Override
	public Generic addType(List<Generic> overrides, Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).addType(unwrap(overrides), value));
	}

	@Override
	public Generic setType(Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).setType(value));
	}

	@Override
	public Generic setType(Generic override, Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).setType(unwrap(override), value));
	}

	@Override
	public Generic setType(List<Generic> overrides, Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).setType(unwrap(overrides), value));
	}

	@Override
	public Generic addTree(Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).addTree(value));
	}

	@Override
	public Generic addTree(Serializable value, int parentsNumber) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).addTree(value, parentsNumber));
	}

	@Override
	public Generic setTree(Serializable value) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).setTree(value));
	}

	@Override
	public Generic setTree(Serializable value, int parentsNumber) {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).setTree(value, parentsNumber));
	}

	@Override
	public Generic getMetaAttribute() {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).getMetaAttribute());
	}

	@Override
	public Generic getMetaRelation() {
		return wrap(((org.genericsystem.concurrency.Engine) unwrap(this)).getMetaRelation());
	}

	@Override
	public void discardWithException(Throwable exception) {
		((org.genericsystem.concurrency.Engine) unwrap(this)).discardWithException(exception);
	}

}
