package org.genericsystem.core.impl;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.genericsystem.core.api.Generic;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public class GenericImpl implements Generic {

	private Supplier<AncestorsService<Generic>> supplier;

	public GenericImpl(Supplier<AncestorsService<Generic>> supplier) {
		this.supplier = supplier;
	}

	// public GenericImpl(final AncestorsService<Generic> service) {
	// this(() -> vertex.getPlugged());
	// }

	public GenericImpl(final Supplier<Generic> metaSupplier, final Supplier<Stream<Generic>> supersSupplier, final Supplier<Serializable> valueSupplier, final Supplier<Stream<Generic>> componentsSupplier) {
		this(() -> new AncestorsService<Generic>() {

			@Override
			public Generic getMeta() {
				return metaSupplier.get();
			}

			@Override
			public Stream<Generic> getSupersStream() {
				return supersSupplier.get();
			}

			@Override
			public Stream<Generic> getComponentsStream() {
				return componentsSupplier.get();
			}

			@Override
			public Serializable getValue() {
				return valueSupplier.get();
			}

		});
	}

	// public void resolve() {
	// supplier = () -> getMeta().getInstance(getSupersStream(), getValue(), getComponents());
	// }

	Supplier<AncestorsService<Generic>> getSupplier() {
		return supplier;
	}

	private static final Function<Vertex, Generic> VERTEX_WRAPPER = GenericImpl::new;

	public GenericImpl(final Vertex vertex) {
		this(() -> new AncestorsService<Generic>() {

			@Override
			public Generic getMeta() {
				return VERTEX_WRAPPER.apply(vertex.getPlugged().getMeta());
			}

			@Override
			public Stream<Generic> getSupersStream() {
				return vertex.getPlugged().getSupersStream().map(VERTEX_WRAPPER);
			}

			@Override
			public Stream<Generic> getComponentsStream() {
				return vertex.getPlugged().getComponentsStream().map(VERTEX_WRAPPER);
			}

			@Override
			public Serializable getValue() {
				return vertex.getValue();
			}

		});
	}

	@Override
	public Generic getMeta() {
		return getSupplier().get().getMeta();
	}

	@Override
	public Stream<Generic> getSupersStream() {
		return getSupplier().get().getSupersStream();
	}

	@Override
	public Stream<Generic> getComponentsStream() {
		return getSupplier().get().getComponentsStream();
	}

	@Override
	public Serializable getValue() {
		return getSupplier().get().getValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Generic))
			return false;
		Generic service = (Generic) obj;
		return this.equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

}
