package org.genericsystem.kernel.services;


public interface RemovableService<T extends RemovableService<T>> extends BindingService<T> {

	boolean isCascadeRemove(int pos);

	@Override
	boolean isReferentialIntegrityConstraintEnabled(int pos);

	void remove();

}
