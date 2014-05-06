package org.genericsystem.kernel.services;

import java.util.stream.Collectors;

public interface DisplayService<T extends AncestorsService<T>> extends AncestorsService<T> {

	default String info() {
		return "(" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + getComponentsStream().collect(Collectors.toList()) + " ";
	}

}
