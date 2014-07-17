package org.genericsystem.kernel.services;

public interface VertexService<T extends VertexService<T, U>, U extends RootService<T, U>> extends AncestorsService<T, U>, DependenciesService<T, U>, BindingService<T, U>, DisplayService<T, U>, SystemPropertiesService<T, U>,
		CompositesInheritanceService<T, U>, WriteService<T, U>, MapService<T, U>, ApiService<T, U> {

}