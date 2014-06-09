package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.genericsystem.kernel.services.DependenciesService;

public class SupersComputer<T extends DependenciesService<T>> extends LinkedHashSet<T> {

	private static final long serialVersionUID = -1078004898524170057L;

	private final int level;
	private final T meta;
	private final List<T> overrides;
	private final List<T> components;
	private final Serializable value;

	private final Map<T, Boolean> alreadyComputed = new HashMap<>();

	public SupersComputer(int level, T meta, List<T> overrides, Serializable value, List<T> components) {
		this.level = level;
		this.meta = meta;
		this.overrides = overrides;
		this.components = components;
		this.value = value;
		visit(meta.getRoot());
	}

	private boolean visit(T candidate) {
		Boolean result = alreadyComputed.get(candidate);
		if (result != null)
			return result;
		boolean isMeta = meta.isSpecializationOf(candidate);
		boolean isSuper = !isMeta && candidate.isSuperOf(meta, overrides, value, components);
		if (!isMeta && !isSuper) {
			alreadyComputed.put(candidate, false);
			return false;
		}
		boolean selectable = true;
		for (T inheriting : candidate.getInheritings())
			if (visit(inheriting))
				selectable = false;
		if (isMeta)
			for (T instance : candidate.getInstances())
				if (visit(instance))
					selectable = false;

		result = alreadyComputed.put(candidate, selectable);
		assert result == null;
		if (selectable && candidate.getLevel() == level && !candidate.equiv(meta, value, components))
			add(candidate);
		return selectable;
	}
}
