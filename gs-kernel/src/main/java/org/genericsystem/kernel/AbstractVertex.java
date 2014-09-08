package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Statics.Supers;
import org.genericsystem.kernel.exceptions.AliveConstraintViolationException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.NotFoundException;
import org.genericsystem.kernel.exceptions.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.services.RootService;
import org.genericsystem.kernel.services.VertexService;

public abstract class AbstractVertex<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends Signature<T, U> implements VertexService<T, U> {

	protected List<T> supers;

	protected abstract Dependencies<T> getInstancesDependencies();

	protected abstract Dependencies<T> getInheritingsDependencies();

	protected abstract Dependencies<DependenciesEntry<T>> getMetaComposites();

	protected abstract Dependencies<DependenciesEntry<T>> getSuperComposites();

	@SuppressWarnings("unchecked")
	protected T init(boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		super.init(throwExistException, meta, value, components);
		this.supers = supers;
		checkDependsMetaComponents();
		checkSupers(supers);
		checkDependsSuperComponents(supers);
		return (T) this;
	}

	protected T newT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT().init(throwExistException, meta, supers, value, components);
	}

	private void checkDependsMetaComponents() {
		Serializable value = getValue();
		// TODO KK
		if (value.equals(SystemMap.class) || (value instanceof AxedPropertyClass && ((AxedPropertyClass) value).getClazz().equals(PropertyConstraint.class)))
			return;
		assert getMeta().getComponents() != null;
		if (!(getMeta().componentsDepends(getComponents(), getMeta().getComponents())))
			getRoot().discardWithException(new IllegalStateException("Inconsistant components : " + getComponents() + " " + getMeta().getComponents()));
	}

	private void checkSupers(List<T> supers) {
		supers.forEach(Signature::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers : " + getSupers()));
		if (!supers.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers : " + getSupers()));
		if (!supers.stream().noneMatch(this::equals))
			getRoot().discardWithException(new IllegalStateException("Supers loop detected : " + info()));
	}

	private void checkDependsSuperComponents(List<T> supers) {
		getSupersStream().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), supers, getValue(), getComponents()))
				getRoot().discardWithException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}

	@Override
	public List<T> getSupers() {
		return supers;
	}

	@SuppressWarnings("static-method")
	protected <H> Dependencies<H> buildDependencies() {
		return new DependenciesImpl<>();
	}

	protected void forceRemove() {
		computeDependencies().forEach(this::simpleRemove);
	}

	private void simpleRemove(T vertex) {
		if (!vertex.isAlive())
			getRoot().discardWithException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		if (!vertex.getInstances().isEmpty() || !vertex.getInheritings().isEmpty() || !vertex.getComposites().isEmpty())
			getRoot().discardWithException(new IllegalStateException(vertex.info() + " has dependencies"));
		vertex.unplug();
	}

	private LinkedHashSet<T> buildOrderedDependenciesToRemove() throws ReferentialIntegrityConstraintViolationException {
		@SuppressWarnings("unchecked")
		T restructoratorService = (T) this;
		return new LinkedHashSet<T>() {
			private static final long serialVersionUID = -3610035019789480505L;
			{
				visit(restructoratorService);
			}

			// TODO clean
			public void visit(T generic) throws ReferentialIntegrityConstraintViolationException {
				if (add(generic)) {// protect from loop
					if (!generic.getInheritings().isEmpty() || !generic.getInstances().isEmpty())
						throw new ReferentialIntegrityConstraintViolationException("Ancestor : " + generic + " has an inheritance or instance dependency");

					for (T composite : generic.getComposites())
						if (!generic.equals(composite)) {
							for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
								if (!/* compositeDependency.isAutomatic() && */composite.getComponents().get(componentPos).equals(generic) && !contains(composite) && composite.isReferentialIntegrityConstraintEnabled(componentPos))
									throw new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + generic + " by component position : " + componentPos);
							visit(composite);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (generic.isCascadeRemove(axe))
							visit(generic.getComponents().get(axe));
				}
			}
		};
	}

	private Iterable<T> reverseLinkedHashSet(LinkedHashSet<T> linkedHashSet) {
		List<T> dependencies = new ArrayList<>(linkedHashSet);
		Collections.reverse(dependencies);
		return dependencies;
	}

	private Iterable<T> getOrderedDependenciesToRemove() throws ConstraintViolationException {
		return reverseLinkedHashSet(buildOrderedDependenciesToRemove());
	}

	@Override
	public void remove() {
		try {
			getOrderedDependenciesToRemove().forEach(x -> this.simpleRemove(x));
		} catch (ConstraintViolationException e) {
			getRoot().discardWithException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T update(List<T> supersToAdd, Serializable newValue, T... newComponents) {
		return update(supersToAdd, newValue, Arrays.asList(newComponents));
	}

	protected T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents) {
		if (newComponents.size() != getComponents().size())
			getRoot().discardWithException(new IllegalArgumentException());
		return rebuildAll(() -> getMeta().bindInstance(null, isThrowExistException(), new Supers<>(getSupers(), supersToAdd), newValue, newComponents), computeDependencies());
	}

	private static class ConvertMap<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		T convert(T dependency) {
			if (dependency.isAlive())// KK ?
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				T meta = (dependency.isRoot()) ? dependency : convert(dependency.getMeta());
				newDependency = meta.buildInstance(null, dependency.isThrowExistException(), dependency.getSupersStream().map(x -> convert(x)).collect(Collectors.toList()), dependency.getValue(),
						dependency.getComponentsStream().map(x -> x.equals(this) ? null : convert(x)).collect(Collectors.toList())).plug();
				put(dependency, newDependency);
			}
			return newDependency;
		}
	}

	protected LinkedHashSet<T> computeDependencies() {
		return new DependenciesComputer<T, U>() {
			private static final long serialVersionUID = 4116681784718071815L;

			@Override
			boolean checkDependency(T node) {
				return isAncestorOf(node);
			}
		}.visit(getMeta());
	}

	@SuppressWarnings("unchecked")
	protected LinkedHashSet<T> computePotentialDependencies(Serializable value, List<T> components) {
		return new DependenciesComputer<T, U>() {
			private static final long serialVersionUID = -3611136800445783634L;

			@Override
			boolean checkDependency(T node) {
				return node.dependsFrom((T) AbstractVertex.this, value, components);
			}
		}.visit((T) this);
	}

	public T bindInstance(Class<?> clazz, boolean throwExistException, List<T> overrides, Serializable value, List<T> components) {
		checkSameEngine(components);
		checkSameEngine(overrides);
		T nearestMeta = adjustMeta(overrides, value, components);
		if (nearestMeta != this)
			return nearestMeta.bindInstance(clazz, throwExistException, overrides, value, components);
		T weakInstance = getEquivInstance(value, components);
		if (weakInstance != null)
			if (throwExistException)
				getRoot().discardWithException(new ExistsException("Attempts to add an already existing instance : " + weakInstance.info()));
			else
				return weakInstance.equals(this, value, components) ? weakInstance : weakInstance.update(overrides, value, components);
		return rebuildAll(() -> buildInstance(clazz, throwExistException, overrides, value, components).plug(), nearestMeta.computePotentialDependencies(value, components));
	}

	private void checkSameEngine(List<T> generics) {
		if (generics.stream().anyMatch(generic -> !generic.getRoot().equals(getRoot())))
			getRoot().discardWithException(new CrossEnginesAssignementsException());
	}

	@SuppressWarnings("unchecked")
	private T rebuildAll(Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		ConvertMap<T, U> convertMap = new ConvertMap<>();
		dependenciesToRebuild.forEach(this::simpleRemove);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(this);
		convertMap.put((T) this, build);
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));
		return build;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T addInstance(List<T> overrides, Serializable value, T... components) {
		return bindInstance(null, true, overrides, value, Arrays.asList(components));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T setInstance(List<T> overrides, Serializable value, T... components) {
		return bindInstance(null, false, overrides, value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInheritings(final T origin, final int level) {
		return () -> new InheritanceComputer<>((T) AbstractVertex.this, origin, level).inheritanceIterator();
	}

	abstract protected T newT();

	abstract protected T[] newTArray(int dim);

	@Override
	@SuppressWarnings("unchecked")
	public T[] coerceToArray(Object... array) {
		T[] result = newTArray(array.length);
		for (int i = 0; i < array.length; i++)
			result[i] = (T) array[i];
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] targetsToComponents(T... targets) {
		T[] components = newTArray(targets.length + 1);
		components[0] = (T) this;
		System.arraycopy(targets, 0, components, 1, targets.length);
		return components;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	T buildInstance(Class<?> clazz, boolean throwExistException, List<T> overrides, Serializable value, List<T> components) {
		int level = getLevel() == 0 && Objects.equals(getValue(), getRoot().getValue()) && getComponentsStream().allMatch(c -> c.isRoot()) && Objects.equals(value, getRoot().getValue()) && components.stream().allMatch(c -> c.isRoot()) ? 0 : getLevel() + 1;
		overrides.forEach(Signature::checkIsAlive);
		components.forEach(Signature::checkIsAlive);
		List<T> supers = new ArrayList<>(new SupersComputer(level, this, overrides, value, components));
		checkOverridesAreReached(overrides, supers);
		return newT(clazz, throwExistException, (T) this, supers, value, components);
	}

	void checkOverridesAreReached(List<T> overrides, List<T> supers) {
		if (!Statics.areOverridesReached(overrides, supers))
			getRoot().discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
	}

	static <T extends VertexService<T, U>, U extends RootService<T, U>> boolean componentsDepends(SingularsLazyCache singulars, List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if (subComponent.isSpecializationOf(superComponent)) {
					if (singulars.get(subIndex))
						return true;
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		return true;
	}

	static interface SingularsLazyCache {
		boolean get(int i);
	}

	@Override
	public boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		class SingularsLazyCacheImpl implements SingularsLazyCache {
			private final Boolean[] singulars = new Boolean[subComponents.size()];

			@Override
			public boolean get(int i) {
				return singulars[i] != null ? singulars[i] : (singulars[i] = isSingularConstraintEnabled(i));
			}
		}
		return componentsDepends(new SingularsLazyCacheImpl(), subComponents, superComponents);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || isSuperOf(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	@Override
	public boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents) {
		return isSuperOf(getMeta(), getValue(), getComponents(), superMeta, superValue, superComponents);
	}

	private static <T extends AbstractVertex<T, U>, U extends RootService<T, U>> boolean isSuperOf(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!subMeta.componentsDepends(subComponents, superComponents))
			return false;
		if (!subMeta.isPropertyConstraintEnabled())
			return Objects.equals(subValue, superValue);
		return true;
	}

	@Override
	public Snapshot<T> getComposites() {
		return () -> getMetaComposites().stream().map(entry -> entry.getValue().stream()).flatMap(x -> x).iterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T meta) {
		return () -> {
			for (DependenciesEntry<T> entry : getMetaComposites())
				if (meta.equals(entry.getKey()))
					return entry.getValue().iterator();
			return Collections.emptyIterator();
		};
	}

	@Override
	public Snapshot<T> getSuperComposites(T superT) {
		return () -> {
			for (DependenciesEntry<T> entry : getSuperComposites())
				if (superT.equals(entry.getKey()))
					return entry.getValue().iterator();
			return Collections.emptyIterator();
		};
	}

	@SuppressWarnings("unchecked")
	public <subT extends T> subT plug() {
		T result = ((AbstractVertex<T, U>) getMeta()).indexInstance((T) this);
		getSupersStream().forEach(superGeneric -> ((AbstractVertex<T, U>) superGeneric).indexInheriting((T) this));
		getComponentsStream().forEach(component -> ((AbstractVertex<T, U>) component).indexByMeta(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> ((AbstractVertex<T, U>) component).indexBySuper(superGeneric, (T) this)));
		return (subT) result;
	}

	@SuppressWarnings("unchecked")
	public boolean unplug() {
		boolean result = ((AbstractVertex<T, U>) getMeta()).unIndexInstance((T) this);
		if (!result)
			getRoot().discardWithException(new NotFoundException(this.info()));
		getSupersStream().forEach(superGeneric -> ((AbstractVertex<T, U>) superGeneric).unIndexInheriting((T) this));
		getComponentsStream().forEach(component -> ((AbstractVertex<T, U>) component).unIndexByMeta(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> ((AbstractVertex<T, U>) component).unIndexBySuper(superGeneric, (T) this)));
		return result;
	}

	private T indexByMeta(T meta, T composite) {
		return index(getMetaComposites(), meta, composite);
	}

	private T indexBySuper(T superVertex, T composite) {
		return index(getSuperComposites(), superVertex, composite);
	}

	private static <T extends AbstractVertex<T, U>, U extends RootService<T, U>> T index(Dependencies<DependenciesEntry<T>> multimap, T index, T composite) {
		for (DependenciesEntry<T> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().set(composite);

		Dependencies<T> dependencies = composite.buildDependencies();
		T result = dependencies.set(composite);
		multimap.set(new DependenciesEntry<>(index, dependencies));
		return result;
	}

	private static <T> boolean unIndex(Dependencies<DependenciesEntry<T>> multimap, T index, T composite) {
		for (DependenciesEntry<T> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().remove(composite);
		return false;
	}

	private boolean unIndexByMeta(T meta, T composite) {
		return unIndex(getMetaComposites(), meta, composite);
	}

	private boolean unIndexBySuper(T superT, T composite) {
		return unIndex(getSuperComposites(), superT, composite);
	}

	private static <T> T index(Dependencies<T> dependencies, T dependency) {
		return dependencies.set(dependency);
	}

	private static <T> boolean unIndex(Dependencies<T> dependencies, T dependency) {
		return dependencies.remove(dependency);
	}

	@Override
	public Snapshot<T> getInstances() {
		return getInstancesDependencies();
	}

	@Override
	public Snapshot<T> getInheritings() {
		return getInheritingsDependencies();
	}

	private T indexInstance(T instance) {
		return index(getInstancesDependencies(), instance);
	}

	private T indexInheriting(T inheriting) {
		return index(getInheritingsDependencies(), inheriting);
	}

	private boolean unIndexInstance(T instance) {
		return unIndex(getInstancesDependencies(), instance);
	}

	private boolean unIndexInheriting(T inheriting) {
		return unIndex(getInheritingsDependencies(), inheriting);
	}

}
