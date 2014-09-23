package org.genericsystem.kernel;

import java.util.Collections;
import org.genericsystem.api.exception.RollbackException;

public interface IRoot<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertex<T, U> {

	public enum CheckingType {
		CHECK_ON_ADD_NODE, CHECK_ON_REMOVE_NODE
	}

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	default void check(CheckingType checkingType, boolean isFlushTime, T t) throws RollbackException {
		t.checkDependsMetaComponents();
		t.checkSupers();
		t.checkDependsSuperComponents();
		checkConsistency(checkingType, isFlushTime, t);
		checkConstraints(checkingType, isFlushTime, t);
	}

	@SuppressWarnings("unchecked")
	default T getMetaAttribute() {
		return ((T) this).getDirectInstance(getValue(), Collections.singletonList((T) this));
	}

	//
	// These signatures force Engine to re-implement methods
	//
	default void checkConsistency(CheckingType checkingType, boolean isFlushTime, T t) {
	}

	default void checkConstraints(CheckingType checkingType, boolean isFlushTime, T t) {
	}

	@Override
	boolean isRoot();

	@Override
	public U getRoot();

	@Override
	public T getAlive();

}
