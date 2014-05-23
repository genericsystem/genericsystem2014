package org.genericsystem.concurrency;

import org.genericsystem.kernel.services.FactoryService;

public interface ConcurrencyService<T extends ConcurrencyService<T>> extends FactoryService<T> {

}
