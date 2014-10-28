package org.genericsystem.kernel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InheritanceComputer<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends HashSet<T> {

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

	Stream<T> inheritanceStream() {
		return getInheringsStream(base);
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
			return localBase.getSupers().stream().filter(next -> /* base.getMeta().equals(next.getMeta()) && */origin.isCompositeOf(next));
		}

		private Stream<T> fromAboveStream() {
			if (!origin.isCompositeOf(localBase))
				return Stream.empty();
			if (baseSupersStream().count() == 0)
				return localBase.isRoot() || !origin.isCompositeOf(localBase.getMeta()) ? Stream.of(origin) : getInheringsStream(localBase.getMeta());
			return Statics.concat(baseSupersStream(), superVertex -> getInheringsStream(superVertex)).distinct();
		}

		private Stream<T> projectStream(Stream<T> streamToProject) {
			return Statics.concat(streamToProject, holder -> getStream(holder)).distinct();
		}

		private Stream<T> getStream(final T holder) {
			if (holder.getLevel() != level || localBase.getSuperComposites(holder).iterator().hasNext())
				add(holder);
			Stream<T> indexStream = Stream.concat(holder.getLevel() < level ? localBase.getMetaComposites(holder).get() : Stream.empty(), localBase.getSuperComposites(holder).get());
			return Stream.concat(isTerminal() && contains(holder) ? Stream.empty() : Stream.of(holder), projectStream(indexStream));
		}
	}
}
