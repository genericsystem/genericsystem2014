package org.genericsystem.concurrency;

import org.genericsystem.kernel.TimestampDependencies;

public class Generic extends AbstractGeneric<Generic> implements DefaultGeneric<Generic> {

	private final TimestampDependencies<Generic> instancesDependencies = buildDependencies();
	private final TimestampDependencies<Generic> inheritingsDependencies = buildDependencies();
	private final TimestampDependencies<Generic> compositesDependencies = buildDependencies();

	@Override
	protected TimestampDependencies<Generic> getInstancesDependencies() {
		return instancesDependencies;
	}

	@Override
	protected TimestampDependencies<Generic> getInheritingsDependencies() {
		return inheritingsDependencies;
	}

	@Override
	protected TimestampDependencies<Generic> getCompositesDependencies() {
		return compositesDependencies;
	}

	@Override
	protected TimestampDependencies<Generic> buildDependencies() {
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
