package org.genericsystem.kernel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InheritanceComputer<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends HashSet<T> {

	private static final long serialVersionUID = 1877502935577170921L;

	private final Map<T, Collection<T>> inheritings = new HashMap<>();

	private final T base;
	private final T origin;
	private final int level;

	public InheritanceComputer(T base, T origin, int level) {
		this.base = base;
		this.origin = origin;
		this.level = level;
	}

	Iterator<T> inheritanceIterator() {
		return getInheringsStream(base).iterator();
	}

	private Stream<T> getInheringsStream(T superVertex) {
		Collection<T> result = inheritings.get(superVertex);
		if (result == null)
			inheritings.put(superVertex, result = new Inheritings(superVertex).inheritanceStream().collect(Collectors.toList()));
		return result.stream();
	}

	private class Inheritings {

		private final T localBase;

		private Inheritings(T localBase) {
			this.localBase = localBase;
		}

		private boolean isTerminal() {
			return localBase.equals(base);
		}

		protected Stream<T> inheritanceStream() {
			return projectStream(fromAboveStream());
		}

		private Stream<T> baseSupersStream() {
			assert localBase.getSupers() != null : localBase.info();
			return localBase.getSupersStream().filter(next -> /* base.getMeta().equals(next.getMeta()) && */origin.isAttributeOf(next));
		}

		private Stream<T> fromAboveStream() {
			if (origin == null)
				System.out.println("origin is null");
			if (!origin.isAttributeOf(localBase))
				return Stream.empty();
			Stream<T> supersStream = baseSupersStream();
			if (!baseSupersStream().iterator().hasNext())
				return (localBase.isRoot() || !origin.isAttributeOf(localBase.getMeta())) ? Stream.of(origin) : getInheringsStream(localBase.getMeta());
				return Statics.concat(supersStream, superVertex -> getInheringsStream(superVertex)).distinct();
		}

		protected Stream<T> projectStream(Stream<T> streamToProject) {
			return Statics.concat(streamToProject, holder -> getStream(holder)).distinct();
		}

		protected Stream<T> getStream(final T holder) {
			if (holder.getLevel() != level || localBase.getSuperComposites(holder).iterator().hasNext())
				add(holder);
			Stream<T> indexStream = Stream.concat(holder.getLevel() < level ? localBase.getMetaComposites(holder).stream() : Stream.empty(), localBase.getSuperComposites(holder).stream());
			return Stream.concat(isTerminal() && contains(holder) ? Stream.empty() : Stream.of(holder), projectStream(indexStream));
		}
	}
}
