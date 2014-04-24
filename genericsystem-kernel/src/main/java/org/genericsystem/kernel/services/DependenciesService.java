package org.genericsystem.kernel.services;

import java.util.Arrays;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Snapshot.AbstractSnapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.exceptions.ExistException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface DependenciesService extends AncestorsService, FactoryService {

	public static abstract class Dependencies extends AbstractSnapshot<Vertex> {

		abstract protected boolean remove(Vertex vertex);

		abstract protected void add(Vertex vertex);

		public Vertex set(Vertex vertex) {
			Vertex result = get(vertex);
			if (result == null) {
				add(vertex);
				return vertex;
			}
			return result;
		}
	}

	Dependencies getInstances();

	Dependencies getInheritings();

	Dependencies getComposites();

	default Snapshot<Vertex> getMetaComposites(Vertex meta) {
		return getComposites().filter(composite -> composite.getMeta().equals(meta));
	}

	default Snapshot<Vertex> getSuperComposites(Vertex superVertex) {
		return getComposites().filter(composite -> composite.getSupersStream().anyMatch(next -> next.equals(superVertex)));
	}

	default boolean isPlugged() throws NotFoundException {
		return this == getPlugged();
	}

	default Vertex getPlugged() {
		return getMeta().getInstances().get((Vertex) this);
	}

	default Vertex plug(boolean throwsExistException) {
		Vertex vertex = getMeta().getInstances().set((Vertex) this);
		if (this != vertex) {
			if (throwsExistException)
				throw new ExistException(vertex);
			return vertex;
		}
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().set((Vertex) this));
		Arrays.asList(getComponents()).forEach(component -> component.getComposites().set((Vertex) this));
		return vertex;
	}

	default boolean unplug() throws NotFoundException {
		boolean result = getMeta().getInstances().remove((Vertex) this);
		if (!result)
			throw new NotFoundException((Vertex) this);
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().remove((Vertex) this));
		Arrays.asList(getComponents()).forEach(component -> component.getComposites().remove((Vertex) this));
		return result;
	}
}
