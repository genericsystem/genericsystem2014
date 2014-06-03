package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.stream.Collectors;

import org.genericsystem.kernel.Restructurator;

public interface Updatable<T extends Updatable<T>> extends BindingService<T> {

	@SuppressWarnings("unchecked")
	default T setValue(Serializable value) {
		return new Restructurator<T>() {
			private static final long serialVersionUID = -2459793038860672894L;

			@Override
			protected T rebuild() {
				T meta = getMeta();
				return buildInstance().init(meta.getLevel() + 1, meta, getSupersStream().collect(Collectors.toList()), value, getComponents()).plug();
			}
		}.rebuildAll((T) Updatable.this, computeAllDependencies());
	}

}
