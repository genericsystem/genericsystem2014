package org.genericsystem.kernel;

import org.genericsystem.api.core.IVertex;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultAncestors<T, U>, DefaultDependencies<T, U>, DefaultDisplay<T, U>, DefaultSystemProperties<T, U>, DefaultComponentsInheritance<T, U>, DefaultWritable<T, U>, IVertex<T, U> {

}