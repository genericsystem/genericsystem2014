package org.genericsystem.api.core;



public interface IContext<T extends IVertex<T>> {
	
	IRoot<T> getRoot();

	boolean isAlive(T vertex);

	Snapshot<T> getInheritings(T vertex);

	Snapshot<T> getInstances(T vertex);

	Snapshot<T> getComposites(T vertex);

}
