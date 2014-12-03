package org.genericsystem.concurrency;

import org.genericsystem.kernel.Dependencies;

public class Generic extends AbstractGeneric<Generic> implements DefaultGeneric<Generic> {

	private final Dependencies<Generic> instancesDependencies = buildDependencies();
	private final Dependencies<Generic> inheritingsDependencies = buildDependencies();
	private final Dependencies<Generic> compositesDependencies = buildDependencies();

	@Override
	protected Dependencies<Generic> getInstancesDependencies() {
		return instancesDependencies;
	}

	@Override
	protected Dependencies<Generic> getInheritingsDependencies() {
		return inheritingsDependencies;
	}

	@Override
	protected Dependencies<Generic> getCompositesDependencies() {
		return compositesDependencies;
	}

	@Override
	protected Dependencies<Generic> buildDependencies() {
		return new AbstractTimestampedDependencies<Generic>() {

			@Override
			public LifeManager getLifeManager() {
				return Generic.this.getLifeManager();
			}
		};
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}
}
