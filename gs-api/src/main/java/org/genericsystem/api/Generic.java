package org.genericsystem.api;

import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.FactoryService;

public interface Generic extends AncestorsService<Generic>, DisplayService<Generic>, FactoryService<Generic> {

}
