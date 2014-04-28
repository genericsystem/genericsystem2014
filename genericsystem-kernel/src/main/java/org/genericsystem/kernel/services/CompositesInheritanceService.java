package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public interface CompositesInheritanceService {

	default Snapshot<Vertex> getAttributes(Vertex attribute) {
		return getInheritings(attribute, 1);
	}

	default Snapshot<Vertex> getHolders(Vertex attribute) {
		return getInheritings(attribute, 2);
	}

	default Snapshot<Serializable> getValues(Vertex attribute) {
		return getInheritings(attribute, 2).project(holder -> holder.getValue());
	}

	default Snapshot<Vertex> getInheritings(final Vertex origin, final int level) {
		return new AbstractSnapshot<Vertex>() {
			@Override
			public Iterator<Vertex> iterator() {
				return inheritingsIterator(origin, level);
			}
		};
	}

	default Iterator<Vertex> inheritingsIterator(final Vertex origin, final int level) {
		class Forbidden extends HashSet<Vertex> {

			private static final long serialVersionUID = 1877502935577170921L;

			private final Map<Vertex, Collection<Vertex>> inheritings = new HashMap<>();

			private final Vertex origin;
			private final int level;

			public Forbidden(Vertex origin, int level) {
				this.origin = origin;
				this.level = level;
			}

			private Iterator<Vertex> inheritanceIterator() {
				return getInheringsStream((Vertex) CompositesInheritanceService.this).iterator();
			};

			private Stream<Vertex> getInheringsStream(Vertex superVertex) {
				Collection<Vertex> result = inheritings.get(superVertex);
				if (result == null)
					inheritings.put(superVertex, result = new Inheritings(superVertex).inheritanceStream().collect(Collectors.toList()));
				return result.stream();
			}

			class Inheritings {

				private final Vertex base;

				private Inheritings(Vertex base) {
					this.base = base;
				}

				private boolean isTerminal() {
					return base.equals(CompositesInheritanceService.this);
				}

				protected Stream<Vertex> inheritanceStream() {
					return projectStream(fromAboveStream());
				}

				private Stream<Vertex> supersStream() {
					return base.getSupersStream().filter(next -> base.getMeta().equals(next.getMeta()) && origin.isAttributeOf(next));
				}

				private Stream<Vertex> fromAboveStream() {
					if (!origin.isAttributeOf(base))
						return Stream.empty();
					Stream<Vertex> supersStream = supersStream();
					if (!supersStream().iterator().hasNext())
						return (base.isRoot() || !origin.isAttributeOf(base.getMeta())) ? Stream.of(origin) : getInheringsStream(base.getMeta());
						return Statics.concat(supersStream, superVertex -> getInheringsStream(superVertex)).distinct();
				}

				protected Stream<Vertex> projectStream(Stream<Vertex> streamToProject) {
					return Statics.concat(streamToProject, holder -> getStream(holder)).distinct();
				}

				protected Stream<Vertex> getStream(final Vertex holder) {
					if (holder.getLevel() != level || base.getSuperComposites(holder).iterator().hasNext())
						add(holder);
					Stream<Vertex> indexStream = Stream.concat(holder.getLevel() < level ? base.getMetaComposites(holder).stream() : Stream.empty(), base.getSuperComposites(holder).stream());
					return Stream.concat(isTerminal() && contains(holder) ? Stream.empty() : Stream.of(holder), projectStream(indexStream));
				}
			}
		}
		return new Forbidden(origin, level).inheritanceIterator();
		// protected Iterator<Vertex> inheritanceIterator() {
		// return inheritanceStream().iterator();
		// // return projectIterator(fromAboveIterator());
		// }
		//
		// private Iterator<Vertex> supersIterator() {
		// return base.getSupersStream().filter(next -> base.getMeta().equals(next.getMeta()) && origin.isAttributeOf(next)).iterator();
		// }
		//
		// private Iterator<Vertex> fromAboveIterator() {
		// if (!origin.isAttributeOf(base))
		// return Collections.emptyIterator();
		// Iterator<Vertex> supersIterator = supersIterator();
		// if (!supersIterator.hasNext())
		// return (base.isEngine() || !origin.isAttributeOf(base.getMeta())) ? new SingletonIterator<Vertex>(origin) : new Inheritings(base.getMeta()).inheritanceIterator();
		//
		// return new AbstractConcateIterator<Vertex, Vertex>(supersIterator) {
		// @Override
		// protected Iterator<Vertex> getIterator(Vertex superVertex) {
		// return buildInheritings(superVertex).inheritanceIterator();
		// }
		// };
		// }
		//
		// protected Iterator<Vertex> projectIterator(Iterator<Vertex> iteratorToProject) {
		// return new AbstractConcateIterator<Vertex, Vertex>(iteratorToProject) {
		// @Override
		// protected Iterator<Vertex> getIterator(final Vertex holder) {
		// Iterator<Vertex> indexIterator = holder.getLevel() < level ? new ConcateIterator<>(base.getMetaComposites(holder).iterator(), base.getSuperComposites(holder).iterator()) : base.getSuperComposites(holder).iterator();
		// if (holder.getLevel() != level || base.getSuperComposites(holder).iterator().hasNext())
		// add(holder);
		// return projectIterator(indexIterator, holder);
		// }
		// };
		// }
		//
		// protected Iterator<Vertex> projectIterator(Iterator<Vertex> indexIterator, Vertex holder) {
		// return isTerminal() && contains(holder) ? projectIterator(indexIterator) : new ConcateIterator<>(new SingletonIterator<Vertex>(holder), projectIterator(indexIterator));
		// }

	}
}
