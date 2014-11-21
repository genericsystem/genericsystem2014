package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultVertex<T extends DefaultVertex<T>> extends DefaultAncestors<T>, DefaultDependencies<T>, DefaultDisplay<T>, DefaultSystemProperties<T>, DefaultCompositesInheritance<T>, DefaultWritable<T>, IVertex<T> {

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
		return setHolder(getMeta(), value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T addInheritingNode(Serializable value) {
		return addHolder(getMeta(), (T) this, value, coerceToTArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T setInheritingNode(Serializable value) {
		return setHolder(getMeta(), (T) this, value, coerceToTArray());
	}

	@Override
	default Snapshot<T> getSubNodes() {
		return () -> getComposites().get().filter(x -> x.getMeta().equals(getMeta()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getAllSubNodes() {
		return () -> Stream.concat(Stream.of((T) this), getSubNodes().get().flatMap(node -> node.getAllSubNodes().get())).distinct();
	}

	@SuppressWarnings("unchecked")
	default T getAlive() {
		if (isRoot())
			return (T) this;
		if (isMeta()) {
			T aliveMeta = getSupers().get(0).getAlive();
			if (aliveMeta != null)
				for (T inheritings : aliveMeta.getInheritings())
					if (equals(inheritings))
						return inheritings;
		} else {
			T aliveMeta = getMeta().getAlive();
			if (aliveMeta != null)
				for (T instance : aliveMeta.getInstances())
					if (equals(instance))
						return instance;
		}
		return null;
	}

}