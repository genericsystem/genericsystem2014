package org.genercisystem.impl;

import org.genericsystem.impl.GenericService;
import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	public CompositesDependencies<Generic> getMetaComposites() {
		return getVertex().getCompositesByMeta().projectComposites(this::wrap, GenericService::unwrap);
	}

	public CompositesDependencies<Generic> getSuperComposites() {
		return getVertex().getCompositesBySuper().projectComposites(this::wrap, GenericService::unwrap);
	}

	@Override
	public Snapshot<Generic> getComposites() {
		return () -> Statics.concat(getMetaComposites().stream(), entry -> entry.getValue().stream()).iterator();
	}

	@Override
	public Snapshot<Generic> getCompositesByMeta(Generic meta) {
		return getMetaComposites().getByIndex(meta);
	}

	@Override
	public Snapshot<Generic> getCompositesBySuper(Generic superGeneric) {
		return getSuperComposites().getByIndex(superGeneric);
	}

	@Override
	public void setCompositeByMeta(Generic meta, Generic composite) {
		getMetaComposites().setByIndex(meta, composite);
	}

	@Override
	public void setCompositeBySuper(Generic superGeneric, Generic composite) {
		getSuperComposites().setByIndex(superGeneric, composite);
	}

	@Override
	public void removeCompositeByMeta(Generic meta, Generic composite) {
		getMetaComposites().removeByIndex(meta, composite);
	}

	@Override
	public void removeCompositeBySuper(Generic superGeneric, Generic composite) {
		getSuperComposites().removeByIndex(superGeneric, composite);
	}

}
