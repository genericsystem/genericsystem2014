package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.defaults.DefaultRoot;
import org.genericsystem.kernel.Config.MetaAttribute;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.kernel.Config.Sequence;
import org.genericsystem.kernel.Config.SystemMap;
import org.genericsystem.kernel.Generic.GenericImpl;

public class Root extends GenericImpl implements DefaultRoot<Generic> {

	private final TsGenerator generator = new TsGenerator();
	private Context context;
	private final SystemCache systemCache;
	private final Archiver archiver;
	private final Map<Generic, Vertex> map = new ConcurrentHashMap<>();

	private boolean initialized = false;

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(value, null, userClasses);
	}

	@Override
	public Root getRoot() {
		return this;
	}

	public Root(Serializable value, String persistentDirectoryPath, Class<?>... userClasses) {
		init(this, LifeManager.TS_SYSTEM, null, Collections.emptyList(), value, Collections.emptyList(), LifeManager.SYSTEM_TS);
		startContext();
		systemCache = new SystemCache(this, getClass());
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class, Sequence.class), userClasses);
		flushContext();
		archiver = new Archiver(this, persistentDirectoryPath);
		initialized = true;
		shiftContext();
	}

	protected void startContext() {
		context = new Transaction(this, pickNewTs());
	}

	protected void flushContext() {
		// //Autoflush
	}

	protected void shiftContext() {
		context = new Transaction(this, pickNewTs());
	}

	public long pickNewTs() {
		return generator.pickNewTs();
	}

	boolean isInitialized() {
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
	public Context getCurrentCache() {
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

	Context buildTransaction() {
		return new Transaction(this, pickNewTs());
	}

	@Override
	public Generic getMap() {
		return find(SystemMap.class);
	}

	private Vertex getVertex(Generic generic) {
		return map.get(generic);
	}

	@Override
	public Generic getSequence() {
		return find(Sequence.class);

	}

	long getTs(Generic generic) {
		return getVertex(generic).getTs();
	}

	Generic getMeta(Generic generic) {
		return getVertex(generic).getMeta();
	}

	Generic getNextDependency(Generic generic, Generic ancestor) {
		return getVertex(generic).getNextDependency(ancestor);
	}

	void setNextDependency(Generic generic, Generic ancestor, Generic nextDependency) {
		getVertex(generic).setNextDependency(ancestor, nextDependency);
	}

	LifeManager getLifeManager(Generic generic) {
		return getVertex(generic).getLifeManager();
	}

	List<Generic> getSupers(Generic generic) {
		return getVertex(generic).getSupers();
	}

	Serializable getValue(Generic generic) {
		return getVertex(generic).getValue();
	}

	List<Generic> getComponents(Generic generic) {
		return getVertex(generic).getComponents();
	}

	Dependencies getDependencies(Generic generic) {
		return getVertex(generic).getDependencies();
	}

	Generic init(Generic generic, long ts, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
		Vertex result = map.putIfAbsent(generic, new Vertex(generic, ts, meta, supers, value, components, otherTs));
		assert result == null;
		return ((GenericImpl) generic).init(Root.this);
	}
}
