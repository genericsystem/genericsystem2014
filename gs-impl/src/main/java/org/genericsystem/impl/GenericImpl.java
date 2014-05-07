package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public class GenericImpl implements Generic {

	public static final Function<Vertex, Generic> VERTEX_WRAPPER = v -> {
		// assert !v.isRoot() : v.info();
		return v.isRoot() ? new EngineImpl((Root) v) : new GenericImpl(v);
	};

	private final Generic meta;
	private final Generic[] supers;
	private final Generic[] components;
	private final Serializable value;

	public GenericImpl(Generic meta, Generic[] supers, Serializable value, Generic... components) {
		this.meta = meta;
		this.supers = supers;
		this.value = value;
		this.components = components;
	}

	public GenericImpl(Vertex vertex) {
		this(VERTEX_WRAPPER.apply(vertex.getAlive().getMeta()), vertex.getAlive().getSupersStream().map(VERTEX_WRAPPER).toArray(Generic[]::new), vertex.getValue(), vertex.getAlive().getComponentsStream().map(VERTEX_WRAPPER).toArray(Generic[]::new));
	}

	@Override
	public Generic getMeta() {
		return meta;
	}

	@Override
	public Stream<Generic> getSupersStream() {
		return Arrays.stream(supers);
	}

	@Override
	public Stream<Generic> getComponentsStream() {
		return Arrays.stream(components);
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Generic))
			return false;
		Generic service = (Generic) obj;
		return equiv(service);
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

	@Override
	public Snapshot<Generic> getInstances() {
		return getAlive().getInstances().project(VERTEX_WRAPPER);
	}

	@Override
	public Snapshot<Generic> getInheritings() {
		return getAlive().getInheritings().project(VERTEX_WRAPPER);
	}

}
