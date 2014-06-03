package org.genericsystem.concurrency;

import java.util.Iterator;

import org.genericsystem.kernel.services.AncestorsService;

public interface AncestorsConcurrencyService<T extends AncestorsService<T>> {

	default VertexConcurrency getVertexConcurrency() {
		@SuppressWarnings("unchecked")
		VertexConcurrency pluggedMeta = ((AncestorsConcurrencyService<T>) getMeta()).getVertexConcurrency();
		if (pluggedMeta == null)
			return null;
		Iterator<VertexConcurrency> it = pluggedMeta.getInstances().iterator();
		while (it.hasNext()) {
			VertexConcurrency next = it.next();
			if (equiv(next))
				return next;
		}
		return null;
	}

	// default boolean equiv(AncestorsService<? extends AncestorsService<?>> service) {
	// return service == null ? false : equiv(service.getMeta(), service.getValue(), service.getComponents());
	// }
	//
	// default boolean equiv(AncestorsService<?> meta, Serializable value, List<? extends AncestorsService<?>> components) {
	// return this.getMeta().equiv(meta) && Objects.equals(getValue(), value) && equivComponents(getComponents(), components);
	// }
	//
	// static boolean equivComponents(List<? extends AncestorsService<?>> components, List<? extends AncestorsService<?>> otherComponents) {
	// if (otherComponents.size() != components.size())
	// return false;
	// Iterator<? extends AncestorsService<?>> otherComponentsIt = otherComponents.iterator();
	// return components.stream().allMatch(x -> x.equiv(otherComponentsIt.next()));
	// }

}
