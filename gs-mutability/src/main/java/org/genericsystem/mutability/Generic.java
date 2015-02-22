package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.defaults.DefaultVertex;

public interface Generic extends DefaultVertex<Generic> {

	default Engine getEngine() {
		throw new IllegalStateException();
	}

	@Override
	default Cache getCurrentCache() {
		return getEngine().getCurrentCache();
	}

	@Override
	default Generic getMeta() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getMeta());
	}

	@Override
	default List<Generic> getSupers() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getSupers());
	}

	@Override
	default Serializable getValue() {
		return getCurrentCache().unwrap(this).getValue();
	}

	@Override
	default List<Generic> getComponents() {
		return getCurrentCache().wrap(getCurrentCache().unwrap(this).getComponents());
	}

	@Override
	default String info() {
		return getCurrentCache().unwrap(this).info();
	}

}
