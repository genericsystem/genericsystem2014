package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.GenericHandler;
import org.genericsystem.kernel.Root;

public class LazyHandler extends GenericHandler implements Generic {

	public LazyHandler(Cache cache, Generic meta, List<Generic> overrides, Serializable value, List<Generic> components) {
		super(cache, meta, overrides, value, components);
		cache.plug(this);
	}

	@Override
	protected Generic resolve() {
		if (resolved)
			return get();
		resolved = true;
		((Cache) context).unplug(this);
		return add();
	}

	@Override
	public Root getRoot() {
		return context.getRoot();
	}

	@Override
	public boolean isSystem() {
		return false;
	}

	@Override
	public Generic getMeta() {
		return adjustedMeta;
	}

	@Override
	public List<Generic> getSupers() {
		return supers;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public List<Generic> getComponents() {
		return components;
	}

	@Override
	public boolean isMeta() {
		return super.isMeta();
	}

	@Override
	public Generic get() {
		return super.get();
	}
}
