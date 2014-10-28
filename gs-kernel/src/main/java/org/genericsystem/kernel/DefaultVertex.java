package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.stream.Stream;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultAncestors<T, U>, DefaultDependencies<T, U>, DefaultDisplay<T, U>, DefaultSystemProperties<T, U>, DefaultComponentsInheritance<T, U>,
		DefaultWritable<T, U>, IVertex<T, U> {

	@Override
	default T addRoot(Serializable value) {
		return addInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T setRoot(Serializable value) {
		return setInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T addSubNode(Serializable value) {
		return addHolder(getMeta(), value, coerceToTArray());
	}

	@Override
	default T setSubNode(Serializable value) {
		return addHolder(getMeta(), value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T addInheritingSubNode(Serializable value) {
		return addHolder(getMeta(), (T) this, value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T setInhertingSubNode(Serializable value) {
		return setHolder((T) this, getMeta(), value, coerceToTArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getSubNodes() {
		return () -> ((T) this).getMetaComposites(this.getMeta()).stream().iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getAllSubNodes() {
		return () -> Stream.concat(Stream.of((T) this), getSubNodes().stream().flatMap(inheriting -> inheriting.getAllSubNodes().stream())).distinct().iterator();
	}
}