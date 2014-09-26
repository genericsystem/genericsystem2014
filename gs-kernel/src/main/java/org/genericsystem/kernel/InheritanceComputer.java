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
			return localBase.getSupers().stream().filter(next -> /* base.getMeta().equals(next.getMeta()) && */origin.isComponentOf(next));
		}

		private Stream<T> fromAboveStream() {
			if (!origin.isComponentOf(localBase))
				return Stream.empty();
			if (baseSupersStream().count() == 0)
				return localBase.isRoot() || !origin.isComponentOf(localBase.getMeta()) ? Stream.of(origin) : getInheringsStream(localBase.getMeta());
				return Statics.concat(baseSupersStream(), superVertex -> getInheringsStream(superVertex)).distinct();
		}

		private Stream<T> projectStream(Stream<T> streamToProject) {
			return Statics.concat(streamToProject, holder -> getStream(holder)).distinct();
		}

		private Stream<T> getStream(final T holder) {
			if (holder.getLevel() != level || localBase.getSuperComponents(holder).iterator().hasNext())
				add(holder);
			Stream<T> indexStream = Stream.concat(holder.getLevel() < level ? localBase.getMetaComponents(holder).stream() : Stream.empty(), localBase.getSuperComponents(holder).stream());
			return Stream.concat(isTerminal() && contains(holder) ? Stream.empty() : Stream.of(holder), projectStream(indexStream));
		}
	}
}
