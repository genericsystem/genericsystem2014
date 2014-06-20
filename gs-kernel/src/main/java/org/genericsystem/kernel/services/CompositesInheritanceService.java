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

public interface CompositesInheritanceService<T extends CompositesInheritanceService<T>> extends BindingService<T> {

	default Snapshot<T> getMetaAttributes(T attribute) {
		return getInheritings(attribute, 0);
	}

	default Snapshot<T> getAttributes(T attribute) {
		return getInheritings(attribute, 1);
	}

	default Snapshot<T> getHolders(T attribute) {
		return getInheritings(attribute, 2);
	}

	default Snapshot<Serializable> getValues(T attribute) {
		return getHolders(attribute).project(T::getValue);
	}

	default Snapshot<T> getInheritings(final T origin, final int level) {
		return new AbstractSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return inheritingsIterator(origin, level);
			}
		};
	}

	default Iterator<T> inheritingsIterator(final T origin, final int level) {
		class Forbidden extends HashSet<T> {

			private static final long serialVersionUID = 1877502935577170921L;

			private final Map<T, Collection<T>> inheritings = new HashMap<>();

			private final T origin;
			private final int level;

			public Forbidden(T origin, int level) {
				this.origin = origin;
				this.level = level;
			}

			@SuppressWarnings("unchecked")
			private Iterator<T> inheritanceIterator() {
				return getInheringsStream((T) CompositesInheritanceService.this).iterator();
			}

			private Stream<T> getInheringsStream(T superVertex) {
				Collection<T> result = inheritings.get(superVertex);
				if (result == null)
					inheritings.put(superVertex, result = new Inheritings(superVertex).inheritanceStream().collect(Collectors.toList()));
				return result.stream();
			}

			class Inheritings {

				private final T base;

				private Inheritings(T base) {
					this.base = base;
				}

				private boolean isTerminal() {
					return base.equals(CompositesInheritanceService.this);
				}

				protected Stream<T> inheritanceStream() {
					return projectStream(fromAboveStream());
				}

				private Stream<T> baseSupersStream() {
					assert base.getSupers() != null : base.info();
					return base.getSupersStream().filter(next -> /* base.getMeta().equals(next.getMeta()) && */origin.isAttributeOf(next));
				}

				private Stream<T> fromAboveStream() {
					if (!origin.isAttributeOf(base))
						return Stream.empty();
					Stream<T> supersStream = baseSupersStream();
					if (!baseSupersStream().iterator().hasNext())
						return (base.isRoot() || !origin.isAttributeOf(base.getMeta())) ? Stream.of(origin) : getInheringsStream(base.getMeta());
					return Statics.concat(supersStream, superVertex -> getInheringsStream(superVertex)).distinct();
				}

				protected Stream<T> projectStream(Stream<T> streamToProject) {
					return Statics.concat(streamToProject, holder -> getStream(holder)).distinct();
				}

				protected Stream<T> getStream(final T holder) {
					if (holder.getLevel() != level || base.getSuperComposites(holder).iterator().hasNext())
						add(holder);
					Stream<T> indexStream = Stream.concat(holder.getLevel() < level ? base.getMetaComposites(holder).stream() : Stream.empty(), base.getSuperComposites(holder).stream());
					return Stream.concat(isTerminal() && contains(holder) ? Stream.empty() : Stream.of(holder), projectStream(indexStream));
				}
			}
		}
		return new Forbidden(origin, level).inheritanceIterator();
	}
}
