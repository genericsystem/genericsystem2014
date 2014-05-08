package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public class GenericImpl implements Generic {

	private final Generic meta;
	private final Generic[] supers;
	private final Generic[] components;
	private final Serializable value;
	public static final Generic[] EMPTY_GENERICS = new Generic[] {};

	GenericImpl(Generic meta, Generic[] supers, Serializable value, Generic... components) {
		this.meta = meta == null ? this : meta;
		this.supers = supers;
		this.value = value;
		this.components = components;
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
		return getAlive().getInstances().project(this::getGeneric);
	}

	@Override
	public Snapshot<Generic> getInheritings() {
		return getAlive().getInheritings().project(this::getGeneric);
	}

	protected Generic getGeneric(Vertex vertex) {
		if (vertex.isRoot())
			return getRoot();
		// vertex = vertex.getAlive();// is this necessary ?
		List<Generic> overrides = vertex.getSupersStream().map(this::getGeneric).collect(Collectors.toList());
		List<Generic> components = vertex.getComponentsStream().map(this::getGeneric).collect(Collectors.toList());
		return build(getGeneric(vertex.getMeta()), overrides.toArray(new Generic[overrides.size()]), vertex.getValue(), components.toArray(new Generic[components.size()]));
	}

	@Override
	public Generic build(Generic meta, Generic[] overrides, Serializable value, Generic[] components) {
		return new GenericImpl(meta, overrides, value, components);
	}

}
