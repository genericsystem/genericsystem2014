package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.stream.Stream;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultVertex<T extends AbstractVertex<T>> extends DefaultAncestors<T>, DefaultDependencies<T>, DefaultDisplay<T>, DefaultSystemProperties<T>, DefaultCompositesInheritance<T>, DefaultWritable<T>, IVertex<T> {

	@Override
	default T addRoot(Serializable value) {
		return addInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T setRoot(Serializable value) {
		return setInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T addNode(Serializable value) {
		return addHolder(getMeta(), value, coerceToTArray());
	}

	@Override
	default T setNode(Serializable value) {
		return addHolder(getMeta(), value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T addInheritingNode(Serializable value) {
		return addHolder(getMeta(), (T) this, value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T setInheritingNode(Serializable value) {
		return setHolder((T) this, getMeta(), value, coerceToTArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getSubNodes() {
		return () -> ((T) this).getCompositesByMeta(this.getMeta()).get();
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getAllNodes() {
		return () -> Stream.concat(Stream.of((T) this), getSubNodes().get().flatMap(inheriting -> inheriting.getAllNodes().get())).distinct();
	}
}