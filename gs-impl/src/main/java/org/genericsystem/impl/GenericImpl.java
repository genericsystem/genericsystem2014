package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Snapshot;

public class GenericImpl implements Generic {

	private final Generic meta;
	private final Generic[] supers;
	private final Generic[] components;
	private final Serializable value;

	public GenericImpl(Generic meta, Generic[] supers, Serializable value, Generic... components) {
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
		return getAlive().getInstances().project(getVertexWrapper());
	}

	@Override
	public Snapshot<Generic> getInheritings() {
		return getAlive().getInheritings().project(getVertexWrapper());
	}
}
