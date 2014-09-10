package org.genericsystem.kernel;

import org.genericsystem.kernel.services.IVertexBase;

public interface IVertex<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IAncestors<T, U>, IDependencies<T, U>, IDisplay<T, U>, ISystemProperties<T, U>, ICompositesInheritance<T, U>, IWritable<T, U>, IMap<T, U>, IVertexBase<T, U> {

}