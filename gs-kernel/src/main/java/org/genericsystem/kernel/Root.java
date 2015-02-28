package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.kernel.Config.SystemMap;

public class Root extends Generic implements DefaultRoot<Generic> {

	private final TsGenerator generator = new TsGenerator();
	private final Context<Generic> context;
	private final SystemCache<Generic> systemCache;
	private final Archiver<Generic> archiver;

	private boolean initialized = false;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(Statics.TS_SYSTEM, null, Collections.emptyList(), value, Collections.emptyList(), Statics.SYSTEM_TS);
		context = new Transaction<>(this, pickNewTs());
		systemCache = new SystemCache<>(this, getClass());
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class), userClasses);
		archiver = new Archiver<>(this, persistentDirectoryPath);
		initialized = true;
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public final Generic getMetaAttribute() {
		return find(MetaAttribute.class);
	}

	@Override
	public final Generic getMetaRelation() {
		return find(MetaRelation.class);
	}

	@Override
	public Context<Generic> getCurrentCache() {
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) systemCache.get(clazz);
	}

	@Override
	public Class<?> findAnnotedClass(Generic vertex) {
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
	public Context<Generic> buildTransaction() {
		return new Transaction<Generic>(this, pickNewTs());
	}

	@Override
	public Generic getMap() {
		return find(SystemMap.class);
	}
}
