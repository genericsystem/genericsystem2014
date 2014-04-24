package org.genericsystem.kernel.services;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface DisplayService extends AncestorsService {

	default String info() {
		return "(" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + Arrays.toString(getComponents()) + " ";
	}

}