package org.genericsystem.kernel;

public interface DefaultVertex<T extends AbstractVertex<T>> extends DefaultAncestors<T>, DefaultDependencies<T>, DefaultDisplay<T>, DefaultSystemProperties<T>, DefaultCompositesInheritance<T>, DefaultWritable<T>, DefaultTree<T> {

}