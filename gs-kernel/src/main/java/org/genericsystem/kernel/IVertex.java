package org.genericsystem.kernel;

import org.genericsystem.api.core.IVertexBase;

public interface IVertex<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IAncestors<T, U>, IDependencies<T, U>, IDisplay<T, U>, ISystemProperties<T, U>, IComponentsInheritance<T, U>, IWritable<T, U>, IVertexBase<T, U> {

}