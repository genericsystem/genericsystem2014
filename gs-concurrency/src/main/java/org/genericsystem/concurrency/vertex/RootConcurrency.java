package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.value.BooleanValue;
import org.genericsystem.kernel.annotations.value.IntValue;
import org.genericsystem.kernel.annotations.value.StringValue;

public class RootConcurrency extends VertexConcurrency implements RootServiceConcurrency<VertexConcurrency, RootConcurrency> {

	final Map<Class<?>, VertexConcurrency> systemCache = new HashMap<>();

	private final TsGenerator generator = new TsGenerator();

	public RootConcurrency(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public RootConcurrency(Serializable value, Class<?>... userClasses) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		systemCache.put(Root.class, this);
		initSystemMap();
		for (Class<?> clazz : userClasses)
			find(clazz);
	}

	public void initSystemMap() {
		VertexConcurrency map = buildInstance(Collections.emptyList(), SystemMap.class, Collections.singletonList(this)).plug();
		systemCache.put(SystemMap.class, map);
		map.enablePropertyConstraint();
	}

	@Override
	public RootConcurrency getRoot() {
		return this;
	}

	@Override
	public VertexConcurrency getAlive() {
		return this;
	}

	@Override
	public VertexConcurrency find(Class<?> clazz) {
		VertexConcurrency result = systemCache.get(clazz);
		if (result != null)
			return result;
		if (result == null)
			systemCache.put(clazz, result = findMeta(clazz).setInstance(findOverrides(clazz), findValue(clazz), findComponents(clazz)));
		return result;
	}

	VertexConcurrency findMeta(Class<?> clazz) {
		Meta meta = clazz.getAnnotation(Meta.class);
		return meta == null ? (VertexConcurrency) getRoot() : find(meta.value());
	}

	List<VertexConcurrency> findOverrides(Class<?> clazz) {
		List<VertexConcurrency> overridesVertices = new ArrayList<>();
		org.genericsystem.kernel.annotations.Supers supersAnnotation = clazz.getAnnotation(org.genericsystem.kernel.annotations.Supers.class);
		if (supersAnnotation != null)
			for (Class<?> overrideClass : supersAnnotation.value())
				overridesVertices.add(find(overrideClass));
		return overridesVertices;
	}

	Serializable findValue(Class<?> clazz) {
		BooleanValue booleanValue = clazz.getAnnotation(BooleanValue.class);
		if (booleanValue != null)
			return booleanValue.value();

		IntValue intValue = clazz.getAnnotation(IntValue.class);
		if (intValue != null)
			return intValue.value();

		StringValue stringValue = clazz.getAnnotation(StringValue.class);
		if (stringValue != null)
			return stringValue.value();

		return clazz;
	}

	VertexConcurrency[] findComponents(Class<?> clazz) {
		List<VertexConcurrency> components = new ArrayList<>();
		Components componentsAnnotation = clazz.getAnnotation(Components.class);
		if (componentsAnnotation != null)
			for (Class<?> componentClass : componentsAnnotation.value())
				components.add(find(componentClass));
		return components.toArray(new VertexConcurrency[components.size()]);
	}

	public long pickNewTs() {
		return generator.pickNewTs();
	}

	static class TsGenerator {
		private final long startTime = System.currentTimeMillis() * Statics.MILLI_TO_NANOSECONDS - System.nanoTime();
		private final AtomicLong lastTime = new AtomicLong(0L);

		long pickNewTs() {
			long nanoTs;
			long current;
			for (;;) {
				nanoTs = startTime + System.nanoTime();
				current = lastTime.get();
				if (nanoTs > current)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

}
