package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.kernel.Config.SystemMap;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final TsGenerator generator = new TsGenerator();
	private final Context<Vertex> context;
	private final SystemCache<Vertex> systemCache = new SystemCache<>(this, getClass());
	private final Archiver<Vertex> archiver;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(0L, null, Collections.emptyList(), value, Collections.emptyList(), Statics.SYSTEM_TS);
		context = new Transaction<>(this, pickNewTs());
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
		archiver = new Archiver<>(this, persistentDirectoryPath);
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	@Override
	public final Vertex getMetaAttribute() {
		return find(MetaAttribute.class);
	}

	@Override
	public final Vertex getMetaRelation() {
		return find(MetaRelation.class);
	}

	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Vertex> Custom find(Class<?> clazz) {
		return (Custom) systemCache.get(clazz);
	}

	@Override
	public Class<?> findAnnotedClass(Vertex vertex) {
		return systemCache.getByVertex(vertex);
	}

	@Override
	public void close() {
		archiver.close();
	}

	public static class TsGenerator {
		private final long startTime = System.currentTimeMillis() * Statics.MILLI_TO_NANOSECONDS - System.nanoTime();
		private final AtomicLong lastTime = new AtomicLong(0L);

		public long pickNewTs() {
			long nanoTs;
			long current;
			for (;;) {
				nanoTs = startTime + System.nanoTime();
				current = lastTime.get();
				if (nanoTs - current > 0)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

	@Override
	public Context<Vertex> buildTransaction() {
		return new Transaction<Vertex>(this, pickNewTs());
	}
}
