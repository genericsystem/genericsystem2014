package org.genericsystem.api;

import org.genericsystem.api.services.GenericFactoryService;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.DisplayService;

public interface Generic extends AncestorsService<Generic>, DependenciesService<Generic>, GenericFactoryService<Generic>, DisplayService<Generic> {

}
