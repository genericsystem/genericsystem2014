package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

//TODO revisit this
public class SupersComputer<T extends AbstractVertex<T>> extends LinkedHashSet<T> {

	private static final long serialVersionUID = -1078004898524170057L;

	private final T meta;
	private final List<T> overrides;
	private final List<T> components;
	private final Serializable value;

	private final Map<T, boolean[]> alreadyComputed = new HashMap<>();

	public SupersComputer(T root, T meta, List<T> overrides, Serializable value, List<T> components) {
		Context<T> currentCache = root.getCurrentCache();
		overrides.forEach(x -> currentCache.checker.checkIsAlive(x));
		components.stream().filter(component -> component != null).forEach(x -> currentCache.checker.checkIsAlive(x));
		this.meta = meta;
		this.overrides = overrides;
		this.components = components;
		this.value = value;
		visit(root);
	}

	private boolean[] visit(T candidate) {
		boolean[] result = alreadyComputed.get(candidate);
		if (result != null)
			return result;
		boolean isMeta = meta == null ? false : meta.isSpecializationOf(candidate);
		boolean isSuper = !isMeta && candidate.isSuperOf(meta, overrides, value, components);
		if (!isMeta && !isSuper) {
			boolean[] selectableSelected = new boolean[] { true, false };
			alreadyComputed.put(candidate, selectableSelected);
			return selectableSelected;
		}
		boolean selectable = true;
		for (T inheriting : candidate.getInheritings()) {
			boolean[] subSelectionableSelectioned = visit(inheriting);
			if (!subSelectionableSelectioned[0] || subSelectionableSelectioned[1])
				selectable = false;
		}
		if (isMeta) {
			for (T instance : candidate.getInstances()) {
				boolean[] subSelectableSelected = visit(instance);
				if (!subSelectableSelected[0] || subSelectableSelected[1])
					selectable = false;
			}
		}
		boolean[] selectableSelected = new boolean[] { selectable, true };
		result = alreadyComputed.put(candidate, selectableSelected);
		assert result == null : candidate.info();
		if (selectableSelected[0] && selectableSelected[1] && (candidate.getLevel() == (meta == null ? 0 : meta.getLevel() + 1)) && !candidate.equals(meta, overrides, value, components)) {
			add(candidate);
			assert !candidate.isRoot();
		}
		return selectableSelected;
	}
}
