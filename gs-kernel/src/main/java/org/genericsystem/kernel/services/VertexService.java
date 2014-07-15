package org.genericsystem.kernel.services;


public interface VertexService<T extends VertexService<T>> extends AncestorsService<T>, DependenciesService<T>, BindingService<T>, DisplayService<T>, SystemPropertiesService<T>, CompositesInheritanceService<T>, WriteService<T>, MapService<T> {

}
