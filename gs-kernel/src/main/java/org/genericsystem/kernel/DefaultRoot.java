package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.kernel.Root.Provider;

public interface DefaultRoot extends org.genericsystem.api.defaults.DefaultRoot<Generic> {

	Class<?> findAnnotedClass(Generic vertex);

	boolean isInitialized();

	long pickNewTs();

	Context<Generic> buildTransaction();

	Provider getProvider();

	default long getTs(Generic generic) {
		return getProvider().getTs(generic);
	}

	default Generic getMeta(Generic generic) {
		return getProvider().getMeta(generic);
	}

	default LifeManager getLifeManager(Generic generic) {
		return getProvider().getLifeManager(generic);
	}

	default List<Generic> getSupers(Generic generic) {
		return getProvider().getSupers(generic);
	}

	default Serializable getValue(Generic generic) {
		return getProvider().getValue(generic);
	}

	default List<Generic> getComponents(Generic generic) {
		return getProvider().getComponents(generic);
	}
}
