package org.genericsystem.kernel.services;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Vertex;

public interface DisplayService extends AncestorsService<Vertex> {

	default String info() {
		return "(" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + Arrays.toString(getComponents()) + " ";
	}

}
