package org.genericsystem.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public class GenericImpl implements Generic {

	protected Supplier<AncestorsService<Generic>> supplier;

	public static Function<Vertex, Generic> getVertexWrapper(Vertex vertex) {
		return v -> v.isRoot() ? new EngineImpl() : new GenericImpl(v);
	}

	public GenericImpl(Supplier<AncestorsService<Generic>> supplier) {
		this.supplier = supplier;
	}

	// public GenericImpl(final AncestorsService<Generic> service) {
	// this(() -> vertex.getPlugged());
	// }

	// public GenericImpl(final Supplier<Generic> metaSupplier, final Supplier<Stream<Generic>> supersSupplier, final Supplier<Serializable> valueSupplier, final Supplier<Stream<Generic>> componentsSupplier) {
	// this(metaSupplier.get(), supersSupplier.get(), valueSupplier.get(), componentsSupplier.get());
	// }

	public GenericImpl(Generic _meta, Stream<Generic> _supers, Serializable _value, Stream<Generic> _components) {
		this(() -> new AncestorsService<Generic>() {

			private final Generic meta = _meta;
			private final List<Generic> supers = _supers.collect(Collectors.toList());
			private final List<Generic> components = _components.collect(Collectors.toList());
			private final Serializable value = _value;

			@Override
			public Generic getMeta() {
				return meta;
			}

			@Override
			public Stream<Generic> getSupersStream() {
				return supers.stream();
			}

			@Override
			public Stream<Generic> getComponentsStream() {
				return components.stream();
			}

			@Override
			public Serializable getValue() {
				return value;
			}
		});
	}

	// public void resolve() {
	// supplier = () -> getMeta().getInstance(getSupersStream(), getValue(), getComponents());
	// }

	Supplier<AncestorsService<Generic>> getSupplier() {
		return supplier;
	}

	public GenericImpl(final Vertex vertex) {
		this(() -> new AncestorsService<Generic>() {

			@Override
			public Generic getMeta() {
				return getVertexWrapper(vertex).apply(vertex.getAlive().getMeta());
			}

			@Override
			public Stream<Generic> getSupersStream() {
				return vertex.getAlive().getSupersStream().map(getVertexWrapper(vertex));
			}

			@Override
			public Stream<Generic> getComponentsStream() {
				return vertex.getAlive().getComponentsStream().map(getVertexWrapper(vertex));
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

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

}
