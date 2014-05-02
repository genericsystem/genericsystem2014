package org.genericsystem.core.impl;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.genericsystem.core.api.Generic;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public class GenericImpl implements Generic {

	private Supplier<AncestorsService<Vertex>> supplier;

	private static final Function<Vertex, Generic> VERTEX_WRAPPER = GenericImpl::new;

	public GenericImpl(final Vertex vertex) {
		this(() -> vertex.getPlugged());
	}

	public GenericImpl(final Supplier<Vertex> metaSupplier, final Supplier<Stream<Vertex>> supersSupplier, final Supplier<Serializable> valueSupplier, final Supplier<Vertex[]> componentsSupplier) {
		this(() -> new AncestorsService<Vertex>() {

			@Override
			public Vertex getMeta() {
				return metaSupplier.get();
			}

			@Override
			public Stream<Vertex> getSupersStream() {
				return supersSupplier.get();
			}

			@Override
			public Stream<Vertex> getComponentsStream() {
				return null;
			}

			@Override
			public Vertex[] getComponents() {
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

	public GenericImpl(Supplier<AncestorsService<Vertex>> supplier) {
		this.supplier = supplier;
	}

	Supplier<AncestorsService<Vertex>> getSupplier() {
		return supplier;
	}

	@Override
	public Generic getMeta() {
		return VERTEX_WRAPPER.apply(getSupplier().get().getMeta());
	}

	@Override
	public Stream<Generic> getSupersStream() {
		return getSupplier().get().getSupersStream().map(VERTEX_WRAPPER);
	}

	@Override
	public Stream<Generic> getComponentsStream() {
		return getSupplier().get().getComponentsStream().map(VERTEX_WRAPPER);
	}

	@Override
	public Generic[] getComponents() {
		assert false;// TODO
		return null;
	}

	@Override
	public Serializable getValue() {
		return getSupplier().get().getValue();
	}

}
