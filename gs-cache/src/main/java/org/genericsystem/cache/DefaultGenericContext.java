package org.genericsystem.cache;


public interface DefaultGenericContext<T extends AbstractGeneric<T,?>> extends org.genericsystem.kernel.DefaultContext<T>{
	
	@Override
	DefaultEngine<T,?> getRoot();
}
