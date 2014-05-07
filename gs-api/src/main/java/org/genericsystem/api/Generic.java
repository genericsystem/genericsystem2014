package org.genericsystem.api;

import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.FactoryService;

public interface Generic extends AncestorsService<Generic>, DependenciesService<Generic>, FactoryService<Generic>, DisplayService<Generic> {

}
