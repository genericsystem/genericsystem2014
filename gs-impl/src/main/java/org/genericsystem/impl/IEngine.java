package org.genericsystem.impl;

import org.genericsystem.kernel.IRoot;

public interface IEngine<T extends AbstractGeneric<T, U, ?, ?>, U extends IEngine<T, U>> extends IRoot<T, U>, IGeneric<T, U> {

	<subT extends T> subT find(Class<subT> clazz);

}
