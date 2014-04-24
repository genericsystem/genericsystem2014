package org.genericsystem.kernel.services;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface DisplayService extends AncestorsService {

	default String info() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\t\n******** " + this + " ******** \t\n");
		stringBuilder.append("Meta " + getMeta().getValue() + " \t\n");
		stringBuilder.append("Supers " + getSupersStream().collect(Collectors.toList()) + " \t\n");
		stringBuilder.append("Components " + Arrays.toString(getComponents()) + " \t\n");
		return stringBuilder.toString();

		// return "Meta (" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + Arrays.toString(getComponents()) + " ";
	}

}
