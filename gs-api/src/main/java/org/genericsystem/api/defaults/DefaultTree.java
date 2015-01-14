package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultTree<T extends DefaultVertex<T>> extends IVertex<T> {
	@Override
	default T addRoot(Serializable value) {
		return addInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	default T setRoot(Serializable value) {
		return setInstance(value, coerceToTArray(new Object[getMeta().getComponents().size()]));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addChild(Serializable value, T... targets) {
		return addHolder(getMeta(), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setChild(Serializable value, T... targets) {
		return setHolder(getMeta(), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInheritingChild(Serializable value, T... targets) {
		return addHolder(getMeta(), Arrays.asList(addThisToTargets(targets)), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInheritingChild(Serializable value, T... targets) {
		return setHolder(getMeta(), Arrays.asList(addThisToTargets(targets)), value, targets);
	}

	@Override
	default Snapshot<T> getChildren() {
		return () -> getComposites().get().filter(x -> x.getMeta().equals(getMeta()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getAllChildren() {
		return () -> Stream.concat(Stream.of((T) this), getChildren().get().flatMap(node -> node.getAllChildren().get())).distinct();
	}
}
