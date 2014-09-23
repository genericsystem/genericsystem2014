package org.genericsystem.kernel;

import org.genericsystem.api.core.IVertexBase;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.IRoot.CheckingType;

public interface IVertex<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IAncestors<T, U>, IDependencies<T, U>, IDisplay<T, U>, ISystemProperties<T, U>, ICompositesInheritance<T, U>, IWritable<T, U>, IVertexBase<T, U> {

	@SuppressWarnings("unchecked")
	default T check(CheckingType checkingType, boolean isFlushTime) throws RollbackException {
		getRoot().check(checkingType, isFlushTime, (T) this);
		return (T) this;
	}

}