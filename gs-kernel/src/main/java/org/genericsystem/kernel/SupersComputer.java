package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SupersComputer<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends LinkedHashSet<T> {

	private static final long serialVersionUID = -1078004898524170057L;

	private final int level;
	private final T meta;
	private final List<T> overrides;
	private final List<T> composites;
	private final Serializable value;

	private final Map<T, Boolean> alreadyComputed = new HashMap<>();

	@SuppressWarnings("unchecked")
	public SupersComputer(int level, T meta, List<T> overrides, Serializable value, List<T> composites) {
		this.level = level;
		this.meta = meta;
		this.overrides = overrides;
		this.composites = composites;
		this.value = value;
		visit((T) meta.getRoot());
	}

	private boolean visit(T candidate) {
		Boolean result = alreadyComputed.get(candidate);
		if (result != null)
			return result;
		boolean isMeta = meta.isSpecializationOf(candidate);
		boolean isSuper = !isMeta && candidate.isSuperOf(meta, overrides, value, composites);
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
		assert result == null : candidate.info();
		if (selectable && candidate.getLevel() == level && !candidate.equals(meta, overrides, value, composites))
			add(candidate);
		return selectable;
	}
}
