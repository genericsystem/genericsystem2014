package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.genericsystem.kernel.services.AncestorsService;

public interface DisplayService extends AncestorsService {

	default String info() {
		return " (" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + Arrays.toString(getComponents()) + " ";
	}

}
