package org.genericsystem.kernel;

import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.BindingService;
import org.genericsystem.kernel.services.CompositesInheritanceService;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;
import org.genericsystem.kernel.services.MapService;
import org.genericsystem.kernel.services.SignatureService;
import org.genericsystem.kernel.services.SystemPropertiesService;
import org.genericsystem.kernel.services.UpdatableService;

public interface VertexService<T extends VertexService<T>> extends AncestorsService<T>, DependenciesService<T>, BindingService<T>, DisplayService<T>, SystemPropertiesService<T>, ExceptionAdviserService<T>, CompositesInheritanceService<T>,
		UpdatableService<T>, MapService<T>, SignatureService<T> {

}
