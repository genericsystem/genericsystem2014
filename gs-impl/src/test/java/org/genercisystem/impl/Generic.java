package org.genercisystem.impl;

import org.genericsystem.impl.GenericService;
import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Snapshot;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	// @Phantom
	@Override
	public Snapshot<Generic> getSuperComposites(Generic superT) {
		return GenericService.super.getSuperComposites(superT);
	}

	// @Phantom
	@Override
	public Snapshot<Generic> getMetaComposites(Generic meta) {
		return GenericService.super.getMetaComposites(meta);
	}

	// @Phantom
	@Override
	public Generic plug() {
		return GenericService.super.plug();
	}
}
