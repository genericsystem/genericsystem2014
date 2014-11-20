package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.genericsystem.api.core.IVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statics {

	private static Logger log = LoggerFactory.getLogger(Statics.class);

	private static ThreadLocal<Long> threadDebugged = new ThreadLocal<>();
	public static final int NO_POSITION = -1;
	public static final int BASE_POSITION = 0;
	public static final int TARGET_POSITION = 1;
	public static final int TERNARY_POSITION = 2;

	public final static String ENGINE_VALUE = "Engine";
	public static final long MILLI_TO_NANOSECONDS = 1000000L;

	public static final int ATTEMPT_SLEEP = 15;
	public static final int ATTEMPTS = 50;

	public static final int META = 0;
	public static final int STRUCTURAL = 1;
	public static final int CONCRETE = 2;
	public static final int SENSOR = 3;

	public static final int TYPE_SIZE = 0;
	public static final int ATTRIBUTE_SIZE = 1;
	public static final int RELATION_SIZE = 2;
	public static final int TERNARY_RELATION_SIZE = 3;

	public static final long GARBAGE_PERIOD = 1000L;
	public static final long GARBAGE_INITIAL_DELAY = 1000L;
	public static final long LIFE_TIMEOUT = 1386174608777L;// 30 minutes

	// public static <T> Stream<T> concat(Stream<T>[] array) {
	// return Arrays.stream(array).flatMap(x -> x);
	// }

	// public static <T, U> Stream<T> concat(Stream<U> stream, Function<U, Stream<T>> mappers) {
	// return stream.flatMap(mappers);
	// // return stream.<Stream<Vertex>> map(mappers).reduce(Stream.empty(), Stream::concat)
	// }

	// public static Vertex[] insertIntoArray(Vertex generic, Vertex[] targets, int basePos) {
	// if (basePos < 0 || basePos > targets.length)
	// throw new IllegalStateException("Unable to find a valid base position");
	// Vertex[] result = new Vertex[targets.length + 1];
	// System.arraycopy(targets, 0, result, 0, basePos);
	// result[basePos] = generic;
	// System.arraycopy(targets, basePos, result, basePos + 1, result.length - basePos - 1);
	// return result;
	// }

	public static void debugCurrentThread() {
		threadDebugged.set(System.currentTimeMillis());
	}

	public static void stopDebugCurrentThread() {
		threadDebugged.remove();
	}

	public static boolean isCurrentThreadDebugged() {
		return threadDebugged.get() != null;
	}

	public static void logTimeIfCurrentThreadDebugged(String message) {
		if (isCurrentThreadDebugged())
			log.info(message + " : " + (System.currentTimeMillis() - threadDebugged.get()));
	}

	public static String getMetaLevelString(int metaLevel) {
		switch (metaLevel) {
		case META:
			return "META";
		case STRUCTURAL:
			return "STRUCTURAL";
		case CONCRETE:
			return "CONCRETE";
		case SENSOR:
			return "SENSOR";
		default:
			return "UNKNOWN";
		}
	}

	public static String getCategoryString(int metaLevel, int dim) {
		switch (metaLevel) {
		case Statics.META:
			switch (dim) {
			case Statics.TYPE_SIZE:
				return "MetaType";
			case Statics.ATTRIBUTE_SIZE:
				return "MetaAttribute";
			case Statics.RELATION_SIZE:
				return "MetaRelation";
			default:
				return "MetaNRelation";
			}
		case Statics.STRUCTURAL:
			switch (dim) {
			case Statics.TYPE_SIZE:
				return "Type";
			case Statics.ATTRIBUTE_SIZE:
				return "Attribute";
			case Statics.RELATION_SIZE:
				return "Relation";
			default:
				return "NRelation";
			}
		case Statics.CONCRETE:
			switch (dim) {
			case Statics.TYPE_SIZE:
				return "Instance";
			case Statics.ATTRIBUTE_SIZE:
				return "Holder";
			case Statics.RELATION_SIZE:
				return "Link";
			default:
				return "NLink";
			}
		default:
			return null;
		}
	}

	public static class Supers<T extends AbstractVertex<T>> extends ArrayList<T> {
		private static final long serialVersionUID = 6163099887384346235L;

		public Supers(List<T> adds) {
			adds.forEach(this::add);
		}

		public Supers(List<T> adds, T lastAdd) {
			this(adds);
			add(lastAdd);
		}

		public Supers(List<T> adds, List<T> otherAdds) {
			this(adds);
			otherAdds.forEach(this::add);
		}

		@Override
		public boolean add(T candidate) {
			for (T element : this)
				if (element.inheritsFrom(candidate))
					return false;
			Iterator<T> it = iterator();
			while (it.hasNext())
				if (candidate.inheritsFrom(it.next()))
					it.remove();
			return super.add(candidate);
		}
	}

	public static <T extends IVertex<T>> boolean areOverridesReached(List<T> overrides, List<T> supers) {
		return overrides.stream().allMatch(override -> supers.stream().anyMatch(superVertex -> superVertex.inheritsFrom(override)));
	}

	public static <T extends IVertex<T>> List<T> reverseCollections(Collection<T> linkedHashSet) {
		List<T> dependencies = new ArrayList<>(linkedHashSet);
		Collections.reverse(dependencies);
		return dependencies;
	}

}
