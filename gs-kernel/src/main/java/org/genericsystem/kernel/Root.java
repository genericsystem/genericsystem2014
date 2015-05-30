package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.genericsystem.api.core.annotations.InstanceClass;
import org.genericsystem.defaults.DefaultConfig.MetaAttribute;
import org.genericsystem.defaults.DefaultConfig.MetaRelation;
import org.genericsystem.defaults.DefaultConfig.Sequence;
import org.genericsystem.defaults.DefaultConfig.SystemMap;
import org.genericsystem.defaults.DefaultRoot;
import org.genericsystem.kernel.Generic.GenericImpl;

public class Root extends GenericImpl implements DefaultRoot<Generic> {

	private final TsGenerator generator = new TsGenerator();
	protected Wrapper contextWrapper = buildContextWrapper();
	private final SystemCache systemCache;
	private final Archiver archiver;

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
		contextWrapper.set(newCache());
		systemCache = new SystemCache(this, getClass());
		systemCache.mount(Arrays.asList(MetaAttribute.class, MetaRelation.class, SystemMap.class, Sequence.class), userClasses);
		flushContext();
		archiver = new Archiver(this, persistentDirectoryPath);
		initialized = true;
		// shiftContext();
	}

	public interface Wrapper {
		Context get();

		void set(Context context);
	}

	public class ContextWrapper implements Wrapper {

		private Context context;

		@Override
		public Context get() {
			return context;
		}

		@Override
		public void set(Context context) {
			this.context = context;

		}
	}

	protected Wrapper buildContextWrapper() {
		return new ContextWrapper();
	}

	@Override
	public Context newCache() {
		return new Transaction(this, pickNewTs());
	}

	protected void flushContext() {
		// //Autoflush
	}

	public long pickNewTs() {
		return generator.pickNewTs();
	}

	boolean isInitialized() {
		return initialized;
	}

	@Override
	public Context getCurrentCache() {
		return contextWrapper.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom find(Class<?> clazz) {
		return (Custom) systemCache.find(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Custom extends Generic> Custom bind(Class<?> clazz) {
		return (Custom) systemCache.bind(clazz);
	}

	@Override
	public Class<?> findAnnotedClass(Generic vertex) {
		return systemCache.getClassByVertex(vertex);
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

	private static class VertexWrapper {
		private Vertex vertex;
		private IDependencies<Generic> dependencies;

		VertexWrapper(Vertex vertex, IDependencies<Generic> dependencies) {
			this.vertex = vertex;
			this.dependencies = dependencies;
		}

		Vertex getVertex() {
			return vertex;
		}

		IDependencies<Generic> getDependencies() {
			return dependencies;
		}

	}

	private final Map<Generic, VertexWrapper> map = new ConcurrentHashMap<>();
	private final Map<Long, Generic> idsMap = new ConcurrentHashMap<>();

	Generic getGenericFromTs(long ts) {
		return idsMap.get(ts);
	}

	private Vertex getVertex(Generic generic) {
		return map.get(generic).getVertex();
	}

	@Override
	public long getTs(Generic generic) {
		return getVertex(generic).getTs();
	}

	@Override
	public Generic getMeta(Generic generic) {
		return getGenericFromTs(getVertex(generic).getMeta());
	}

	@Override
	public List<Generic> getSupers(Generic generic) {
		return getVertex(generic).getSupers().stream().map(id -> getGenericFromTs(id)).collect(Collectors.toList());
	}

	@Override
	public Serializable getValue(Generic generic) {
		return getVertex(generic).getValue();
	}

	@Override
	public List<Generic> getComponents(Generic generic) {
		return getVertex(generic).getComponents().stream().map(id -> getGenericFromTs(id)).collect(Collectors.toList());
	}

	IDependencies<Generic> getDependencies(Generic generic) {
		return map.get(generic).getDependencies();
	}

	LifeManager getLifeManager(Generic generic) {
		return getVertex(generic).getLifeManager();
	}

	Generic init(Long ts, Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
		return init(newT(clazz, meta), ts == null ? pickNewTs() : ts, meta, supers, value, components, otherTs);
	}

	private Generic init(Generic generic, long ts, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
		Vertex vertex = new Vertex(ts, meta == null ? ts : meta.getTs(), supers.stream().map(g -> g.getTs()).collect(Collectors.toList()), value, components.stream().map(g -> g.getTs()).collect(Collectors.toList()), otherTs);
		assert generic != null;
		Generic gresult = idsMap.putIfAbsent(ts, generic);
		assert gresult == null;
		VertexWrapper result = map.putIfAbsent(generic, new VertexWrapper(vertex, new AbstractTsDependencies<Generic>() {
			@Override
			public LifeManager getLifeManager() {
				return vertex.getLifeManager();
			}
		}));
		assert result == null;
		return ((GenericImpl) generic).initRoot(Root.this);
	}

	private Generic newT(Class<?> clazz, Generic meta) {
		InstanceClass metaAnnotation = meta == null ? null : getAnnotedClass(meta).getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getCurrentCache().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		try {
			if (clazz == null || !Generic.class.isAssignableFrom(clazz))
				return new GenericImpl().initRoot(this);
			return (Generic) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getCurrentCache().discardWithException(e);
		}
		return null; // Not reached
	}

	private Class<?> getAnnotedClass(Generic vertex) {
		if (vertex.isSystem()) {
			Class<?> annotedClass = findAnnotedClass(vertex);
			if (annotedClass != null)
				return annotedClass;
		}
		return vertex.getClass();
	}
}
