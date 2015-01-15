package org.genericsystem.api.core;

import java.util.List;

public class ApiStatics {
	public static final int NO_POSITION = -1;
	public static final int BASE_POSITION = 0;
	public static final int TARGET_POSITION = 1;
	public static final int TERNARY_POSITION = 2;

	public static final int META = 0;
	public static final int STRUCTURAL = 1;
	public static final int CONCRETE = 2;
	public static final int SENSOR = 3;

	public static final int TYPE_SIZE = 0;
	public static final int ATTRIBUTE_SIZE = 1;
	public static final int RELATION_SIZE = 2;
	public static final int TERNARY_RELATION_SIZE = 3;

	public static <T extends IVertex<T>> boolean areOverridesReached(List<T> supers, List<T> overrides) {
		return overrides.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override)));
	}

}
