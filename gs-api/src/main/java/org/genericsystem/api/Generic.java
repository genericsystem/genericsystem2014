package org.genericsystem.api;

import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.DisplayService;

public interface Generic extends AncestorsService<Generic>, DisplayService<Generic> {

}
