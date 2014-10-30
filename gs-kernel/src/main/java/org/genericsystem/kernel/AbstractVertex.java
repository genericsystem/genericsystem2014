package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.ConsistencyConstraintViolationException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.CrossEnginesAssignementsException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Statics.Supers;
import org.genericsystem.kernel.annotations.Priority;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T> {

	private T meta;
	private List<T> components;
	private Serializable value;
	private boolean throwExistException;

	@Override
	public DefaultRoot<T> getRoot() {
		return getMeta().getRoot();
	}

	@SuppressWarnings("unchecked")
	protected T init(boolean throwExistException, T meta, Serializable value, List<T> components) {
		this.throwExistException = throwExistException;
		if (meta != null) {
			meta.checkIsAlive();
			this.meta = meta;
		} else
			this.meta = (T) this;
		this.value = value;
		this.components = new ArrayList<>(components);
		for (int i = 0; i < components.size(); i++) {
			T component = components.get(i);
			if (component != null) {
				component.checkIsAlive();
				this.components.set(i, component);
			} else
				this.components.set(i, (T) this);
		}
		return (T) this;
	}

	public boolean isThrowExistException() {
		return throwExistException;
	}

	@Override
	public T getMeta() {
		return meta;
	}

	@Override
	public List<T> getComponents() {
		return components;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	@Override
	public int getLevel() {
		return (isRoot() || components.stream().allMatch(c -> c.isRoot()) && Objects.equals(getValue(), getRoot().getValue())) ? 0 : meta.getLevel() + 1;
	}

	protected List<T> supers;

	protected abstract Dependencies<T> getInstancesDependencies();

	protected abstract Dependencies<T> getInheritingsDependencies();

	protected abstract DependenciesMap<T> getMetaCompositesDependencies();

	protected abstract DependenciesMap<T> getSuperCompositesDependencies();

	@SuppressWarnings("unchecked")
	protected T init(boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> composites) {
		init(throwExistException, meta, value, composites);
		this.supers = supers;
		return (T) this;
	}

	protected T newT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> composites) {
		return newT(clazz).init(throwExistException, meta, supers, value, composites);
	}

	protected T newT(Class<?> clazz) {
		return newT();
	}

	@Override
	public List<T> getSupers() {
		return supers;
	}

	@SuppressWarnings("static-method")
	protected Dependencies<T> buildDependencies() {
		return new DependenciesImpl<>();
	}

	@SuppressWarnings("static-method")
	protected DependenciesMap<T> buildDependenciesMap() {
		return new DependenciesMapImpl<>();
	}

	protected void forceRemove() {
		computeDependencies().forEach(this::simpleRemove);
	}

	private void simpleRemove(T vertex) {
		if (!vertex.isAlive())
			getRoot().discardWithException(new AliveConstraintViolationException(vertex.info() + " is not alive"));
		if (!vertex.getInstances().isEmpty() || !vertex.getInheritings().isEmpty() || !vertex.getComposites().isEmpty())
			getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException(vertex.info() + " has dependencies"));
		vertex.unplug();
	}

	private Iterable<T> reverseLinkedHashSet(LinkedHashSet<T> linkedHashSet) {
		List<T> dependencies = new ArrayList<>(linkedHashSet);
		Collections.reverse(dependencies);
		return dependencies;
	}

	private Iterable<T> getOrderedDependenciesToRemove() {
		return reverseLinkedHashSet(buildOrderedDependenciesToRemove());
	}

	@Override
	public void remove() {
		getOrderedDependenciesToRemove().forEach(x -> simpleRemove(x));
	}

	final T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents) {
		if (newComponents.size() != getComponents().size())
			getRoot().discardWithException(new IllegalArgumentException());
		return rebuildAll(() -> getMeta().bindInstance(null, isThrowExistException(), new Supers<>(getSupers(), supersToAdd), newValue, newComponents), computeDependencies());
	}

	private static class ConvertMap<T extends AbstractVertex<T>> extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		T convert(T dependency) {
			if (dependency.isAlive())// KK ?
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				T meta = (dependency.isRoot()) ? dependency : convert(dependency.getMeta());
				List<T> components = dependency.getComponents().stream().map(x -> x.equals(this) ? null : convert(x)).collect(Collectors.toList());
				meta = meta.adjustMeta(dependency.getValue(), components);
				newDependency = meta.buildInstance(null, dependency.isThrowExistException(), dependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList()), dependency.getValue(), components).plug();
				put(dependency, newDependency);
			}
			return newDependency;
		}
	}

	protected LinkedHashSet<T> computeDependencies() {
		return new DependenciesComputer<T>() {
			private static final long serialVersionUID = 4116681784718071815L;

			@Override
			boolean checkDependency(T node) {
				return isAncestorOf(node);
			}
		}.visit(getMeta());
	}

	@SuppressWarnings("unchecked")
	private LinkedHashSet<T> buildOrderedDependenciesToRemove() {
		return new LinkedHashSet<T>() {
			private static final long serialVersionUID = -3610035019789480505L;
			{
				visit((T) AbstractVertex.this);
			}

			// TODO clean
			public void visit(T generic) {
				if (add(generic)) {// protect from loop
					if (!generic.getInheritings().isEmpty() || !generic.getInstances().isEmpty())
						getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException("Ancestor : " + generic + " has an inheritance or instance dependency"));

					for (T composite : generic.getComposites())
						if (!generic.equals(composite)) {
							for (int componentPos = 0; componentPos < composite.getComponents().size(); componentPos++)
								if (/* !componentDependency.isAutomatic() && */composite.getComponents().get(componentPos).equals(generic) && !contains(composite) && composite.isReferentialIntegrityEnabled(componentPos))
									getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException(composite + " is Referential Integrity for ancestor " + generic + " by composite position : " + componentPos));
							visit(composite);
						}
					for (int axe = 0; axe < generic.getComponents().size(); axe++)
						if (generic.isCascadeRemove(axe))
							visit(generic.getComponents().get(axe));
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	protected LinkedHashSet<T> computePotentialDependencies(List<T> overrides, Serializable value, List<T> components) {
		return new DependenciesComputer<T>() {
			private static final long serialVersionUID = -3611136800445783634L;

			@Override
			boolean checkDependency(T node) {
				return node.dependsFrom((T) AbstractVertex.this, overrides, value, components);
			}
		}.visit((T) this);
	}

	public T adjustMeta(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return adjustMeta(value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	T adjustMeta(Serializable value, List<T> components) {
		T result = null;
		for (T directInheriting : getInheritings()) {
			if (isAdjusted(directInheriting, value, components)) {
				if (result == null)
					result = directInheriting;
				else
					getRoot().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
			}
		}
		return result == null ? (T) this : result.adjustMeta(value, components);
	}

	boolean isAdjusted(T directInheriting, Serializable value, List<T> components) {
		return !components.equals(getComponents()) && !directInheriting.equalsRegardlessSupers(this, value, components)/* && Objects.equals(getValue(), directInheriting.getValue()) */
				&& componentsDepends(components, directInheriting.getComponents());
	}

	// TODO KK if a component is null
	T getDirectInstance(Serializable value, List<T> components) {
		for (T instance : getInstances())
			if (((AbstractVertex<?>) instance).equalsRegardlessSupers(this, value, components))
				return instance;
		return null;
	}

	T getDirectInstance(List<T> overrides, Serializable value, List<T> components) {
		T result = getDirectInstance(value, components);
		return result != null && Statics.areOverridesReached(overrides, result.getSupers()) ? result : null;
	}

	// TODO KK should be protected
	public final T bindInstance(Class<?> clazz, boolean throwExistException, List<T> overrides, Serializable value, List<T> components) {
		checkSameEngine(components);
		checkSameEngine(overrides);
		T adjustedMeta = adjustMeta(value, components);
		if (!throwExistException) {
			T equivInstance = adjustedMeta.getDirectEquivInstance(value, components);
			if (equivInstance != null)
				return equivInstance.equalsRegardlessSupers(adjustedMeta, value, components) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : equivInstance.update(overrides, value, components);
		} else {
			T equivInstance = adjustedMeta.getDirectInstance(value, components);
			if (equivInstance != null)
				getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + equivInstance.info()));
		}
		return rebuildAll(() -> adjustedMeta.buildInstance(clazz, throwExistException, overrides, value, components).plug(), adjustedMeta.computePotentialDependencies(overrides, value, components));
	}

	boolean dependsFrom(T meta, List<T> overrides, Serializable value, List<T> components) {
		return inheritsFrom(meta, value, components) || getComponents().stream().filter(component -> component != null && component != this).anyMatch(component -> component.dependsFrom(meta, overrides, value, components))
				|| (!isRoot() && getMeta().dependsFrom(meta, overrides, value, components)) || (!components.isEmpty() && componentsDepends(getComponents(), components) && overrides.stream().anyMatch(override -> override.inheritsFrom(getMeta())));
	}

	T getDirectEquivInstance(Serializable value, List<T> components) {
		for (T instance : getInstances())
			if (instance.equiv(this, value, components))
				return instance;
		return null;
	}

	private final Function<? super ISignature<?>, ? extends IVertex<?>> NULL_TO_THIS = x -> x == null ? this : (IVertex<?>) x;

	boolean equiv(IVertex<?> meta, Serializable value, List<? extends IVertex<?>> components) {
		if (!getMeta().equiv(meta))
			return false;
		if (getComponents().size() != components.size())
			return false;// for the moment, not equivalent when composite size is different
		List<? extends IVertex<?>> notNullComponents = components.stream().map(NULL_TO_THIS).collect(Collectors.toList());
		List<T> componentsList = getComponents();
		for (int i = 0; i < componentsList.size(); i++)
			if (!isReferentialIntegrityEnabled(i) && isSingularConstraintEnabled(i) && componentsList.get(i).equiv(notNullComponents.get(i)))
				return true;
		for (int i = 0; i < componentsList.size(); i++)
			if (!componentsList.get(i).equiv(notNullComponents.get(i)))
				return false;
		if (!meta.isPropertyConstraintEnabled())
			return Objects.equals(getValue(), value);
		return true;
	}

	private void checkSameEngine(List<T> generics) {
		if (generics.stream().anyMatch(generic -> generic != null && !generic.getRoot().equals(getRoot())))
			getRoot().discardWithException(new CrossEnginesAssignementsException());
	}

	@SuppressWarnings("unchecked")
	T rebuildAll(Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		ConvertMap<T> convertMap = new ConvertMap<>();
		dependenciesToRebuild.forEach(this::simpleRemove);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(this);
		convertMap.put((T) this, build);
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));
		return build;
	}

	@SuppressWarnings("unchecked")
	Snapshot<T> getInheritings(final T origin, final int level) {
		return () -> new InheritanceComputer<>((T) AbstractVertex.this, origin, level).inheritanceStream();
	}

	abstract protected T newT();

	abstract protected T[] newTArray(int dim);

	@SuppressWarnings("unchecked")
	@Override
	public T[] coerceToTArray(Object... array) {
		T[] result = newTArray(array.length);
		for (int i = 0; i < array.length; i++)
			result[i] = (T) array[i];
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] addThisToTargets(T... targets) {
		T[] composites = newTArray(targets.length + 1);
		composites[0] = (T) this;
		System.arraycopy(targets, 0, composites, 1, targets.length);
		return composites;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	T buildInstance(Class<?> clazz, boolean throwExistException, List<T> overrides, Serializable value, List<T> components) {
		int level = getLevel() == 0 && Objects.equals(getValue(), getRoot().getValue()) && getComponents().stream().allMatch(c -> c.isRoot()) && Objects.equals(value, getRoot().getValue()) && components.stream().allMatch(c -> c.isRoot()) ? 0
				: getLevel() + 1;
		overrides.forEach(AbstractVertex::checkIsAlive);
		components.stream().filter(x -> x != null).forEach(T::checkIsAlive);
		List<T> supers = new ArrayList<>(new SupersComputer(level, this, overrides, value, components));
		checkOverridesAreReached(overrides, supers);
		return newT(clazz, throwExistException, (T) this, supers, value, components);
	}

	void checkOverridesAreReached(List<T> overrides, List<T> supers) {
		if (!Statics.areOverridesReached(overrides, supers))
			getRoot().discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
	}

	boolean componentsDepends(SingularsLazyCache singulars, List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				assert subComponent != null || superComponent != null;
				if ((subComponent == null && equals(superComponent)) || (superComponent == null && equals(subComponent)) || (subComponent != null && superComponent != null && subComponent.isSpecializationOf(superComponent))) {
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

	boolean componentsDepends(List<T> subComponents, @SuppressWarnings("unchecked") T... superComponents) {
		return componentsDepends(subComponents, Arrays.asList(superComponents));
	}

	boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		class SingularsLazyCacheImpl implements SingularsLazyCache {
			private final Boolean[] singulars = new Boolean[subComponents.size()];

			@Override
			public boolean get(int i) {
				return singulars[i] != null ? singulars[i] : (singulars[i] = isSingularConstraintEnabled(i));
			}
		}
		return componentsDepends(new SingularsLazyCacheImpl(), subComponents, superComponents);
	}

	@SuppressWarnings("unchecked")
	protected boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || isSuperOf(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	protected boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents) {
		return isSuperOf(getMeta(), getValue(), getComponents(), superMeta, superValue, superComponents);
	}

	private static <T extends AbstractVertex<T>> boolean isSuperOf(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!subMeta.componentsDepends(subComponents, superComponents))
			return false;
		if (subMeta.isPropertyConstraintEnabled())
			return !subComponents.equals(superComponents);
		return Objects.equals(subValue, superValue);
	}

	@Override
	public Snapshot<T> getComposites() {
		return () -> getMetaCompositesDependencies().get().flatMap(entry -> entry.getValue().get());
	}

	@SuppressWarnings("unchecked")
	protected <subT extends T> subT plug() {
		T result = ((AbstractVertex<T>) getMeta()).indexInstance((T) this);
		getSupers().forEach(superGeneric -> ((AbstractVertex<T>) superGeneric).indexInheriting((T) this));
		getComponents().stream().filter(component -> !equals(component)).forEach(component -> ((AbstractVertex<T>) component).indexByMeta(getMeta(), (T) this));
		getSupers().forEach(superGeneric -> getComponents().stream().filter(component -> !equals(component)).forEach(component -> ((AbstractVertex<T>) component).indexBySuper(superGeneric, (T) this)));
		getRoot().check(CheckingType.CHECK_ON_ADD, true, (T) this);
		return (subT) result;
	}

	@SuppressWarnings("unchecked")
	protected boolean unplug() {
		getRoot().check(CheckingType.CHECK_ON_REMOVE, true, (T) this);
		boolean result = ((AbstractVertex<T>) getMeta()).unIndexInstance((T) this);
		if (!result)
			getRoot().discardWithException(new NotFoundException(this.info()));
		getSupers().forEach(superGeneric -> ((AbstractVertex<T>) superGeneric).unIndexInheriting((T) this));
		getComponents().stream().filter(component -> !equals(component)).forEach(component -> ((AbstractVertex<T>) component).unIndexByMeta(getMeta(), (T) this));
		getSupers().forEach(superGeneric -> getComponents().stream().filter(component -> !equals(component)).forEach(component -> ((AbstractVertex<T>) component).unIndexBySuper(superGeneric, (T) this)));
		return result;
	}

	private static <T> Snapshot<T> getCompositesByIndex(DependenciesMap<T> multiMap, T index) {
		return () -> {
			Dependencies<T> dependencies = multiMap.getByIndex(index);
			return dependencies != null ? dependencies.get() : Stream.empty();
		};
	}

	@Override
	public Snapshot<T> getCompositesByMeta(T meta) {
		return getCompositesByIndex(getMetaCompositesDependencies(), meta);
	}

	@Override
	public Snapshot<T> getCompositesBySuper(T superT) {
		return getCompositesByIndex(getSuperCompositesDependencies(), superT);
	}

	private T indexByMeta(T meta, T composite) {
		return index(getMetaCompositesDependencies(), meta, composite);
	}

	private T indexBySuper(T superVertex, T composite) {
		return index(getSuperCompositesDependencies(), superVertex, composite);
	}

	public static interface DependenciesMap<T> extends Dependencies<DependenciesEntry<T>> {
		public default Dependencies<T> getByIndex(T index) {
			for (DependenciesEntry<T> entry : this)
				if (index.equals(entry.getKey()))
					return entry.getValue();
			return null;
		}
	}

	public static class DependenciesMapImpl<T> extends DependenciesImpl<DependenciesEntry<T>> implements DependenciesMap<T> {

	}

	private static <T extends AbstractVertex<T>> T index(DependenciesMap<T> multimap, T index, T composite) {
		Dependencies<T> dependencies = multimap.getByIndex(index);
		if (dependencies == null)
			multimap.set(new DependenciesEntry<>(index, dependencies = composite.buildDependencies()));
		return dependencies.set(composite);
	}

	private static <T> boolean unIndex(DependenciesMap<T> multimap, T index, T composite) {
		Dependencies<T> dependencies = multimap.getByIndex(index);
		if (dependencies == null)
			return false;
		return dependencies.remove(composite);
	}

	private boolean unIndexByMeta(T meta, T composite) {
		return unIndex(getMetaCompositesDependencies(), meta, composite);
	}

	private boolean unIndexBySuper(T superT, T composite) {
		return unIndex(getSuperCompositesDependencies(), superT, composite);
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

	boolean equalsRegardlessSupers(IVertex<?> meta, Serializable value, List<? extends IVertex<?>> components) {
		return (isRoot() || getMeta().equals(meta)) && Objects.equals(getValue(), value) && getComponents().equals(components.stream().map(NULL_TO_THIS).collect(Collectors.toList()));
	}

	// TODO clean
	@SuppressWarnings("unchecked")
	T getMap() {
		return getRoot().getMetaAttribute().getDirectInstance(SystemMap.class, Collections.singletonList((T) getRoot()));
	}

	public static class SystemMap {}

	protected boolean equals(ISignature<?> meta, List<? extends ISignature<?>> supers, Serializable value, List<? extends ISignature<?>> components) {
		return (isRoot() || getMeta().equals(meta)) && Objects.equals(getValue(), value) && getComponents().equals(components.stream().map(NULL_TO_THIS).collect(Collectors.toList())) && getSupers().equals(supers);
	}

	protected Stream<T> getKeys() {
		T map = getMap();
		return map != null ? getAttributes(map).get() : Stream.empty();
	}

	Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> Objects.equals(x.getValue(), property)).findFirst();
	}

	void checkSystemConstraints(CheckingType checkingType, boolean isFlushTime) {
		// checkIsAlive();
		checkDependsMetaComponents();
		checkSupers();
		checkDependsSuperComponents();
		checkLevel();
		checkLevelComponents();

	}

	private void checkDependsMetaComponents() {
		if (!(getMeta().componentsDepends(getComponents(), getMeta().getComponents())))
			getRoot().discardWithException(new ConsistencyConstraintViolationException("Inconsistant composites : " + getComponents() + " " + getMeta().getComponents()));
	}

	private void checkLevelComponents() {
		if (getComponents().stream().anyMatch(component -> component.getLevel() > getLevel()))
			getRoot().discardWithException(new ConsistencyConstraintViolationException("Inconsistant level link between composites : level " + getLevel() + " and another"));
	}

	private void checkLevel() {
		if (getLevel() > Statics.CONCRETE)
			getRoot().discardWithException(new ConsistencyConstraintViolationException("Unable to instanciate generic : " + getMeta() + " because it is already concrete."));
	}

	private void checkSupers() {
		supers.forEach(AbstractVertex::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers : " + supers));
		if (!supers.stream().allMatch(superVertex -> getMeta().inheritsFrom(superVertex.getMeta())))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers : " + supers));
		if (!supers.stream().noneMatch(this::equals))
			getRoot().discardWithException(new IllegalStateException("Supers loop detected : " + info()));
		if (supers.stream().anyMatch(superVertex -> Objects.equals(superVertex.getValue(), getValue()) && superVertex.getComponents().equals(getComponents()) && getMeta().inheritsFrom(superVertex.getMeta())))
			getRoot().discardWithException(new IllegalStateException("Collision detected : " + info()));
	}

	private void checkDependsSuperComponents() {
		getSupers().forEach(superVertex -> {
			if (!superVertex.isSuperOf(getMeta(), supers, getValue(), getComponents()))
				getRoot().discardWithException(new IllegalStateException("Inconsistant components : " + getComponents()));
		});
	}

	@SuppressWarnings("unchecked")
	void checkConstraints(CheckingType checkingType, boolean isFlushTime) {
		for (T constraintAttribute : getSortedConstraints()) {
			Optional<T> constraintHolder = getHolders(constraintAttribute).get().findFirst();
			if (constraintHolder.isPresent()) {
				Serializable value = constraintHolder.get().getValue();
				if (value != null && !Boolean.FALSE.equals(value)) {
					Constraint<T> constraint = constraintAttribute.getConstraint();
					if (isCheckable(constraint, checkingType, isFlushTime))
						try {
							constraint.check((T) this, constraintHolder.get().getComponents().get(Statics.BASE_POSITION), value, ((AxedPropertyClass) constraintAttribute.getValue()).getAxe());
						} catch (ConstraintViolationException e) {
							getRoot().discardWithException(e);
						}
				}
			}
		}
	}

	private List<T> getSortedConstraints() {
		return getKeys().filter(x -> x.getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) x.getValue()).getClazz())).sorted(CONSTRAINT_PRIORITY).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	Constraint<T> getConstraint() {
		try {
			return (Constraint<T>) ((AxedPropertyClass) getValue()).getClazz().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			getRoot().discardWithException(e);
		}
		return null;
	}

	int getConstraintPriority() {
		Class<?> clazz = ((AxedPropertyClass) getValue()).getClazz();
		Priority priority = clazz.getAnnotation(Priority.class);
		return priority != null ? priority.value() : 0;

	}

	@SuppressWarnings("unchecked")
	private boolean isCheckable(Constraint<T> constraint, CheckingType checkingType, boolean isFlushTime) {
		return (isFlushTime || constraint.isImmediatelyCheckable()) && constraint.isCheckedAt((T) this, checkingType);
	}

	void checkConsistency(CheckingType checkingType, boolean isFlushTime) {
		// TODO impl
	}

	private static final Comparator<AbstractVertex<?>> CONSTRAINT_PRIORITY = new Comparator<AbstractVertex<?>>() {
		@Override
		public int compare(AbstractVertex<?> constraintHolder, AbstractVertex<?> compareConstraintHolder) {
			return constraintHolder.getConstraintPriority() < compareConstraintHolder.getConstraintPriority() ? -1 : 1;
		}
	};

}
