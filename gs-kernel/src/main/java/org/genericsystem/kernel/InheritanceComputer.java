package org.genericsystem.kernel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InheritanceComputer<T extends AbstractVertex<T>> extends HashSet<T> {

	private static final long serialVersionUID = 1877502935577170921L;
	protected static Logger log = LoggerFactory.getLogger(InheritanceComputer.class);

	private final Map<T, Collection<T>> inheritingsCache = new HashMap<>();

	private final T base;
	private final T origin;
	private final int level;

	InheritanceComputer(T base, T origin, int level) {
		this.base = base;
		this.origin = origin;
		this.level = level;
	}

	Stream<T> inheritanceStream() {
		return getInheringsStream(base).filter(holder -> !contains(holder) && !holder.equals(origin) && holder.getLevel() == level);
	}

	private Stream<T> getInheringsStream(T superVertex) {
		Collection<T> result = inheritingsCache.get(superVertex);
		if (result == null)
			inheritingsCache.put(superVertex, result = new Inheritings(superVertex).inheritanceStream().collect(Collectors.toList()));
		return result.stream();
		// return new Inheritings(superVertex).inheritanceStream();
	}

	private class Inheritings {

		private final T localBase;

		private Inheritings(T localBase) {
			this.localBase = localBase;
		}

		private Stream<T> inheritanceStream() {
			return fromAboveStream().flatMap(holder -> getStream(holder)).distinct();
		}

		private boolean hasIntermediateSuper() {
			return localBase.isRoot() || localBase.getSupers().stream().filter(next -> localBase.getMeta().equals(next.getMeta())).count() != 0;
		}

		private Stream<T> metaAndSupersStream() {
			return Stream.concat(hasIntermediateSuper() ? Stream.empty() : Stream.of(localBase.getMeta()), localBase.getSupers().stream()).distinct();
		}

		private Stream<T> fromAboveStream() {
			return localBase.isRoot() ? Stream.of(origin) : metaAndSupersStream().flatMap(InheritanceComputer.this::getInheringsStream).distinct();
		}

		private Stream<T> getStream(final T holder) {
			if (!localBase.getCompositesBySuper(holder).isEmpty())
				add(holder);
			Stream<T> indexStream = Stream.concat(holder.getLevel() < level ? localBase.getCompositesByMeta(holder).get() : Stream.empty(), localBase.getCompositesBySuper(holder).get());
			return Stream.concat(Stream.of(holder), indexStream.flatMap(x -> getStream(x)).distinct());
		}
	}
}
