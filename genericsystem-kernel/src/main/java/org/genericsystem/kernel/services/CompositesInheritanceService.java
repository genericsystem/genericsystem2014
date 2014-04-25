package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.iterator.AbstractConcateIterator;
import org.genericsystem.kernel.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.kernel.iterator.SingletonIterator;

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

			private final Vertex origin;
			private final int level;

			public Forbidden(Vertex origin, int level) {
				this.origin = origin;
				this.level = level;
			}

			private Iterator<Vertex> inheritanceIterator() {
				return new InheritingsSameBase((Vertex) CompositesInheritanceService.this).inheritanceIterator();
			}

			class Inheritings {

				final Vertex base;

				private Inheritings(Vertex base) {
					this.base = base;
				}

				protected Iterator<Vertex> inheritanceIterator() {
					return projectIterator(fromAboveIterator());
				}

				private Stream<Vertex> supers() {
					return base.getSupersStream().filter(next -> origin.isAttributeOf(next));
				}

				private Iterator<Vertex> fromAboveIterator() {
					if (!origin.isAttributeOf(base))
						return Collections.emptyIterator();
					Iterator<Vertex> supersIterator = supers().iterator();
					if (!supersIterator.hasNext())
						return (base.isEngine() || !origin.isAttributeOf(base.getMeta())) ? new SingletonIterator<Vertex>(origin) : new Inheritings(base.getMeta()).inheritanceIterator();

					Iterator<Vertex> concateIterator = new AbstractConcateIterator<Vertex, Vertex>(supersIterator) {
						@Override
						protected Iterator<Vertex> getIterator(Vertex superVertex) {
							return new Inheritings(superVertex).inheritanceIterator();
						}
					};
					if (supers().noneMatch(next -> base.getMeta().equals(next.getMeta())))
						return new ConcateIterator<Vertex>(concateIterator, new Inheritings(base.getMeta()).inheritanceIterator());
					return concateIterator;
				}

				protected Iterator<Vertex> projectIterator(Iterator<Vertex> iteratorToProject) {
					return new AbstractConcateIterator<Vertex, Vertex>(iteratorToProject) {
						@Override
						protected Iterator<Vertex> getIterator(Vertex holder) {
							Iterator<Vertex> indexIterator = holder.getLevel() < level ? new ConcateIterator<>(base.getMetaComposites(holder).iterator(), base.getSuperComposites(holder).iterator()) : base.getSuperComposites(holder).iterator();
							if (indexIterator.hasNext()) {
								add(holder);
								return nextProjectIterator(indexIterator, holder);
							}
							return endProjectIterator(indexIterator, holder);
						}
					};
				}

				protected Iterator<Vertex> nextProjectIterator(Iterator<Vertex> indexIterator, Vertex holder) {
					return new ConcateIterator<Vertex>(new SingletonIterator<Vertex>(holder), projectIterator(indexIterator));
				}

				protected Iterator<Vertex> endProjectIterator(Iterator<Vertex> indexIterator, Vertex holder) {
					return new SingletonIterator<Vertex>(holder);
				}
			}

			class InheritingsSameBase extends Inheritings {

				private InheritingsSameBase(Vertex base) {
					super(base);
				}

				protected Iterator<Vertex> nextProjectIterator(Iterator<Vertex> indexIterator, Vertex holder) {
					return projectIterator(indexIterator);
				}

				protected Iterator<Vertex> endProjectIterator(Iterator<Vertex> indexIterator, Vertex holder) {
					return holder.getLevel() == level && !contains(holder) ? new SingletonIterator<Vertex>(holder) : Collections.emptyIterator();
				}
			}
		}
		return new Forbidden(origin, level).inheritanceIterator();
	}
}
