package org.genericsystem.impl;

import org.genericsystem.kernel.IVertex;

public interface IGeneric<T extends IGeneric<T, U>, U extends IEngine<T, U>> extends IVertex<T, U> {

}
