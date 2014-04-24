package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public interface BindingService extends AncestorsService, FactoryService {

	default Vertex addInstance(Serializable value, Vertex... components) {
		return addInstance(Statics.EMPTY_VERTICES, value, components);
	}

	default Vertex addInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, overrides, value, components).plug(true);
	}

	default Vertex setInstance(Serializable value, Vertex... components) {
		return setInstance(Statics.EMPTY_VERTICES, value, components);
	}

	default Vertex setInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, overrides, value, components).plug(false);
	}

	default Vertex getInstance(Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, Statics.EMPTY_VERTICES, value, components).getPlugged();
	}

	default Vertex getInstance(Vertex[] supers, Serializable value, Vertex... components) {
		Vertex result = getInstance(value, components);
		if (result != null && Arrays.stream(supers).allMatch(superVertex -> result.inheritsFrom(result)))
			return result;
		return null;
	}
}
