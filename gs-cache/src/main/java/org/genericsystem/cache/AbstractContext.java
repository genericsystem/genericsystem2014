package org.genericsystem.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.genericsystem.cache.exceptions.ConcurrencyControlException;
import org.genericsystem.cache.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.Vertex;

public abstract class AbstractContext {

	public abstract LifeManager getLifeManager(Vertex vertex);

	public abstract <T extends CacheRoot> T getRoot();

	public abstract long getTs();

	public <T extends Vertex> T plug(T vertex) {
		Set<Vertex> componentSet = new HashSet<>();
		for (Vertex component : vertex.getComponents())
			if (componentSet.add(component))
				getComposites(component).add(vertex);
		Set<Vertex> effectiveSupersSet = new HashSet<>();
		vertex.getSupersStream().forEach(effectiveSuper -> {
			if (effectiveSupersSet.add(effectiveSuper))
				getInheritings(effectiveSuper).add(vertex);
		});
		return vertex;
	}

	<T extends Vertex> T unplug(T vertex) {
		Set<Vertex> componentSet = new HashSet<>();
		for (Vertex component : vertex.getComponents())
			if (componentSet.add(component))
				getComposites(component).remove(vertex);
		Set<Vertex> effectiveSupersSet = new HashSet<>();
		vertex.getSupersStream().forEach(effectiveSuper -> {
			if (effectiveSupersSet.add(effectiveSuper))
				getInheritings(effectiveSuper).remove(vertex);
		});
		return vertex;
	}

	abstract TimestampedDependencies getInheritings(Vertex vertex);

	abstract TimestampedDependencies getComposites(Vertex vertex);

	void apply(Iterable<Vertex> adds, Iterable<Vertex> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removeAll(removes);
		addAll(adds);
	}

	final void addAll(Iterable<Vertex> adds) {
		for (Vertex add : adds)
			simpleAdd(add);
	}

	final void removeAll(Iterable<Vertex> removes) {
		for (Vertex remove : removes)
			simpleRemove(remove);
	}

	final void simpleAdd(Vertex add) {
		plug(add);
	}

	final void simpleRemove(Vertex remove) {
		unplug(remove);
	}

	interface TimestampedDependencies {

		void add(Vertex vertex);

		void remove(Vertex vertex);

		Iterator<Vertex> iterator(long ts);
	}
}
