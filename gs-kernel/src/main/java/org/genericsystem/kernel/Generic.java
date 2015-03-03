package org.genericsystem.kernel;

import java.util.Objects;

public class Generic implements DefaultGeneric<Generic> {

	private Root root;

	Generic init(Root root) {
		this.root = root;
		return this;
	}

	@Override
	public Root getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}
}
