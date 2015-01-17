//package org.genericsystem.concurrency;
//
//import org.genericsystem.kernel.Dependencies;
//
//public class Generic extends org.genericsystem.cache.AbstractGeneric<Generic> {
//
//	private final Dependencies<Generic> instancesDependencies = buildDependencies();
//	private final Dependencies<Generic> inheritingsDependencies = buildDependencies();
//	private final Dependencies<Generic> compositesDependencies = buildDependencies();
//
//	@Override
//	protected Dependencies<Generic> getInstancesDependencies() {
//		return instancesDependencies;
//	}
//
//	@Override
//	protected Dependencies<Generic> getInheritingsDependencies() {
//		return inheritingsDependencies;
//	}
//
//	@Override
//	protected Dependencies<Generic> getCompositesDependencies() {
//		return compositesDependencies;
//	}
//
//	public static final class SystemClass extends Generic {
//
//	}
// }
