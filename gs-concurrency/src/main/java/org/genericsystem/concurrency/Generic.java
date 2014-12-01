package org.genericsystem.concurrency;

import java.util.Iterator;
import org.genericsystem.kernel.Dependencies;

public class Generic extends AbstractGeneric<Generic> implements DefaultGeneric<Generic> {

	private final Dependencies<Generic> instances = buildDependencies();
	private final Dependencies<Generic> inheritings = buildDependencies();
	private final Dependencies<Generic> compositesDependencies = buildDependencies();

	@Override
	protected Dependencies<Generic> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected Dependencies<Generic> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected Dependencies<Generic> getCompositesDependencies() {
		return compositesDependencies;
	}

	@Override
	protected Dependencies<Generic> buildDependencies() {
		return new AbstractDependencies<Generic>() {

			@Override
			public LifeManager getLifeManager() {
				return Generic.this.getLifeManager();
			}

			@Override
			public Iterator<Generic> iterator() {
				return iterator(getRoot().getCurrentCache().getTs());
			}
		};
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}
}
