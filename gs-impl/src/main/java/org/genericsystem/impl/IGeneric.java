package org.genericsystem.impl;

import org.genericsystem.kernel.DefaultVertex;

public interface IGeneric<T extends AbstractGeneric<T, U, ?, ?>, U extends IEngine<T, U>> extends DefaultVertex<T, U> {

}
