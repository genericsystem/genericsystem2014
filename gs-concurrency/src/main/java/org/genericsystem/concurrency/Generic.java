package org.genericsystem.concurrency;

import org.genericsystem.kernel.Dependencies;

public class Generic extends AbstractGeneric<Generic> implements DefaultGeneric<Generic> {

	private final AbstractTimestampedDependencies<Generic> instancesDependencies = builTimestampeddDependencies();
	private final AbstractTimestampedDependencies<Generic> inheritingsDependencies = builTimestampeddDependencies();
	private final AbstractTimestampedDependencies<Generic> compositesDependencies = builTimestampeddDependencies();

	@Override
	protected AbstractTimestampedDependencies<Generic> getInstancesTimestampedDependencies() {
		return instancesDependencies;
	}

	@Override
	protected AbstractTimestampedDependencies<Generic> getInheritingsTimestampedDependencies() {
		return inheritingsDependencies;
	}

	@Override
	protected AbstractTimestampedDependencies<Generic> getCompositesTimestampedDependencies() {
		return compositesDependencies;
	}

	private AbstractTimestampedDependencies<Generic> builTimestampeddDependencies() {
		return new AbstractTimestampedDependencies<Generic>() {

			@Override
			public LifeManager getLifeManager() {
				return Generic.this.getLifeManager();
			}
		};
	}

	@Override
	protected Dependencies<Generic> buildDependencies() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Engine getRoot() {
		return (Engine) super.getRoot();
	}
}
