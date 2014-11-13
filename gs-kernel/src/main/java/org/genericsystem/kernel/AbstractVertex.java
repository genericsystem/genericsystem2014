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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.CrossEnginesAssignementsException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.MetaLevelConstraintViolationException;
import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.genericsystem.api.exception.NotFoundException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.Statics.Supers;
import org.genericsystem.kernel.annotations.Priority;
import org.genericsystem.kernel.systemproperty.AxedPropertyClass;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;

public abstract class AbstractVertex<T extends AbstractVertex<T>> implements DefaultVertex<T> {

	private T meta;
	private List<T> components;
	private Serializable value;

	@SuppressWarnings("unchecked")
	@Override
	public DefaultRoot<T> getRoot() {
		return this != meta ? meta.getRoot() : getSupers().isEmpty() ? (DefaultRoot<T>) this : getSupers().get(0).getRoot();
	}

	@SuppressWarnings("unchecked")
	protected T init(T meta, Serializable value, List<T> components) {
		if (meta != null) {
			meta.checkIsAlive();
			this.meta = meta;
		} else
			this.meta = (T) this;
		this.value = value;
		List<T> _components = new ArrayList<>(components);
		for (int i = 0; i < _components.size(); i++) {
			T component = _components.get(i);
			if (component != null) {
				component.checkIsAlive();
			} else
				_components.set(i, (T) this);
		}
		this.components = Collections.unmodifiableList(_components);
		return (T) this;
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

	protected List<T> supers;

	protected abstract Dependencies<T> getInstancesDependencies();

	protected abstract Dependencies<T> getInheritingsDependencies();

	protected abstract Dependencies<T> getCompositesDependencies();

	@SuppressWarnings("unchecked")
	protected T init(T meta, List<T> supers, Serializable value, List<T> composites) {
		init(meta, value, composites);
		this.supers = Collections.unmodifiableList(supers);
		return (T) this;
	}

	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
		return newT(clazz).init(meta, supers, value, composites);
	}

	protected T newT(Class<?> clazz) {
		return newT();
	}

	@Override
	public List<T> getSupers() {
		return supers;
	}

	protected Dependencies<T> buildDependencies() {
		return new DependenciesImpl<>();
	}

	protected void forceRemove() {
		computeDependencies().forEach(T::unplug);
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
		getOrderedDependenciesToRemove().forEach(T::unplug);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T update(List<T> supers, Serializable newValue, T... newComponents) {
		if (newComponents.length != getComponents().size())
			getRoot().discardWithException(new IllegalArgumentException());
		for (int i = 0; i < newComponents.length; i++)
			if (equiv(newComponents[i]))
				newComponents[i] = null;
		return rebuildAll((T) this, () -> getMeta().setInstance(new Supers<>(supers), newValue, newComponents), computeDependencies());
	}

	private static class ConvertMap<T extends AbstractVertex<T>> extends HashMap<T, T> {
		private static final long serialVersionUID = 5003546962293036021L;

		T convert(T dependency) {
			if (dependency.isAlive())// KK ?
				return dependency;
			T newDependency = get(dependency);
			if (newDependency == null) {
				List<T> components = dependency.getComponents().stream().map(x -> x.equals(dependency) ? null : convert(x)).collect(Collectors.toList());
				T meta = dependency.isRoot() ? dependency : !dependency.isMeta() ? convert(dependency.getMeta()) : null;
				if (meta != null)
					meta = meta.adjustMeta(dependency.getValue(), components);// necessary ?
				List<T> supers = dependency.getSupers().stream().map(x -> convert(x)).collect(Collectors.toList());
				if (meta != null) {
					T directInstance = meta.getDirectInstance(dependency.getValue(), components);
					if (directInstance != null)
						newDependency = directInstance;
					else
						newDependency = dependency.build(null, meta, supers, dependency.getValue(), components).plug();
				} else
					// TODO KK
					newDependency = dependency.build(null, meta, supers, dependency.getValue(), components).plug();
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
		if (!components.equals(getComponents()))
			for (T directInheriting : getInheritings()) {
				if (componentsDepends(components, directInheriting.getComponents())) {
					if (result == null)
						result = directInheriting;
					else
						getRoot().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? (T) this : result.adjustMeta(value, components);
	}

	@SuppressWarnings("unchecked")
	T adjustMeta(int dim) {
		assert isMeta();
		int size = getComponents().size();
		if (size > dim)
			return null;
		if (size == dim)
			return (T) this;
		T directInheriting = getInheritings().first();
		return directInheriting != null && directInheriting.getComponents().size() <= dim ? directInheriting.adjustMeta(dim) : (T) this;
	}

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

	@SuppressWarnings("unchecked")
	protected T addInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		checkSameEngine(componentList);
		checkSameEngine(overrides);
		if (isMeta()) {
			T meta = getRoot().setMeta(componentList.size());
			if (meta.equalsRegardlessSupers(meta, value, componentList) && Statics.areOverridesReached(overrides, meta.getSupers()))
				getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + meta.info()));
		}
		T adjustedMeta = adjustMeta(value, components);
		T equivInstance = adjustedMeta.getDirectInstance(value, componentList);
		if (equivInstance != null)
			getRoot().discardWithException(new ExistsException("An equivalent instance already exists : " + equivInstance.info()));
		return rebuildAll(null, () -> adjustedMeta.build(clazz, adjustedMeta, overrides, value, componentList).plug(), adjustedMeta.computePotentialDependencies(overrides, value, componentList));

	}

	@SuppressWarnings("unchecked")
	protected T setInstance(Class<?> clazz, List<T> overrides, Serializable value, T... components) {
		List<T> componentList = Arrays.asList(components);
		checkSameEngine(componentList);
		checkSameEngine(overrides);
		if (isMeta()) {
			T meta = getRoot().setMeta(componentList.size());
			if (meta.equalsRegardlessSupers(meta, value, componentList) && Statics.areOverridesReached(overrides, meta.getSupers()))
				return meta;
		}
		T adjustedMeta = adjustMeta(value, components);
		T equivInstance = adjustedMeta.getDirectEquivInstance(value, componentList);
		if (equivInstance != null)
			return equivInstance.equalsRegardlessSupers(adjustedMeta, value, componentList) && Statics.areOverridesReached(overrides, equivInstance.getSupers()) ? equivInstance : equivInstance.update(overrides, value, components);
		return rebuildAll(null, () -> adjustedMeta.build(clazz, adjustedMeta, overrides, value, componentList).plug(), adjustedMeta.computePotentialDependencies(overrides, value, componentList));
	}

	boolean dependsFrom(T meta, List<T> overrides, Serializable value, List<T> components) {
		return inheritsFrom(meta, value, components) || getComponents().stream().filter(component -> component != null && component != this).anyMatch(component -> component.dependsFrom(meta, overrides, value, components))
				|| (!isMeta() && getMeta().dependsFrom(meta, overrides, value, components)) || (!components.isEmpty() && componentsDepends(getComponents(), components) && overrides.stream().anyMatch(override -> override.inheritsFrom(getMeta())));
	}

	T getDirectEquivInstance(Serializable value, List<T> components) {
		for (T instance : getInstances())
			if (instance.equiv(this, value, components))
				return instance;
		return null;
	}

	boolean equals(ISignature<?> meta, List<? extends ISignature<?>> supers, Serializable value, List<? extends ISignature<?>> components) {
		return equalsRegardlessSupers(meta, value, components) && getSupers().equals(supers);
	}

	boolean equalsRegardlessSupers(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (!Objects.equals(getValue(), value))
			return false;
		if (this == getMeta()) {
			if (meta != null)
				if (meta != meta.getMeta())
					return false;
		} else if (!getMeta().equals(meta))
			return false;

		List<T> componentsList = getComponents();
		if (componentsList.size() != components.size())
			return false;
		for (int i = 0; i < componentsList.size(); i++) {
			ISignature<?> component = components.get(i);
			if (component == null) {
				if (this != componentsList.get(i))
					return false;
			} else if (!(componentsList.get(i)).equals(component))
				return false;
		}
		return true;
	}

	protected boolean genericEquals(ISignature<?> service) {
		if (this == service)
			return true;
		if (!Objects.equals(getValue(), service.getValue()))
			return false;
		if (this.genericEquals(getMeta())) {
			if (service != service.getMeta())
				return false;
		} else if (!getMeta().genericEquals(service.getMeta()))
			return false;

		List<T> componentsList = getComponents();
		if (componentsList.size() != service.getComponents().size())
			return false;
		for (int i = 0; i < componentsList.size(); i++) {
			if (this == componentsList.get(i)) {
				if (service != service.getComponents().get(i))
					return false;
			} else {
				if (service == service.getComponents().get(i))
					return false;
				if (!(componentsList.get(i)).genericEquals(service.getComponents().get(i)))
					return false;
			}
		}
		List<T> supersList = getSupers();
		if (supersList.size() != service.getSupers().size())
			return false;
		for (int i = 0; i < supersList.size(); i++)
			if (!supersList.get(i).genericEquals(service.getSupers().get(i)))
				return false;
		return true;
	}

	boolean equiv(ISignature<?> service) {
		if (this == getMeta()) {
			if (service.getMeta() != service.getMeta().getMeta())
				return false;
		} else if (!getMeta().equiv(service.getMeta()))
			return false;

		if (getComponents().size() != service.getComponents().size())
			return false;
		List<T> componentsList = getComponents();
		for (int i = 0; i < componentsList.size(); i++)
			if (!isReferentialIntegrityEnabled(i) && isSingularConstraintEnabled(i)) {
				ISignature<?> component = service.getComponents().get(i);
				if (service == component)
					return this == componentsList.get(i);
				else {
					if (this == componentsList.get(i))
						return false;
					return (componentsList.get(i).equiv(component));
				}
			}
		for (int i = 0; i < componentsList.size(); i++) {
			ISignature<?> component = service.getComponents().get(i);
			if (service == component) {
				if (this != componentsList.get(i))
					return false;
			} else {
				if (this == componentsList.get(i))
					return false;
				if (!componentsList.get(i).equiv(component))
					return false;
			}
		}
		if (!getMeta().isPropertyConstraintEnabled())
			return Objects.equals(getValue(), service.getValue());
		return true;
	}

	boolean equiv(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (this == getMeta()) {
			if (meta != null && meta != meta.getMeta())
				return false;
		} else if (!getMeta().equiv(meta))
			return false;

		if (getComponents().size() != components.size())
			return false;
		List<T> componentsList = getComponents();
		for (int i = 0; i < componentsList.size(); i++)
			if (!isReferentialIntegrityEnabled(i) && isSingularConstraintEnabled(i)) {
				ISignature<?> component = components.get(i);
				if (component == null)
					return this == componentsList.get(i);
				else
					return componentsList.get(i).equiv(component);
			}
		for (int i = 0; i < componentsList.size(); i++) {
			ISignature<?> component = components.get(i);
			if (component == null) {
				if (this != componentsList.get(i))
					return false;
			} else if (!componentsList.get(i).equiv(component))
				return false;
		}
		if (!getMeta().isPropertyConstraintEnabled())
			return Objects.equals(getValue(), value);
		return true;
	}

	// TODO move this check in system constraints!
	private void checkSameEngine(List<T> generics) {
		if (generics.stream().anyMatch(generic -> generic != null && !generic.getRoot().equals(getRoot())))
			getRoot().discardWithException(new CrossEnginesAssignementsException());
	}

	static <T extends AbstractVertex<T>> T rebuildAll(T toRebuild, Supplier<T> rebuilder, LinkedHashSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.forEach(T::unplug);
		T build = rebuilder.get();
		dependenciesToRebuild.remove(toRebuild);
		ConvertMap<T> convertMap = new ConvertMap<>();
		convertMap.put(toRebuild, build);
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

	@SuppressWarnings("unchecked")
	T build(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		// TODO chechMeta if meta is null
		overrides.forEach(AbstractVertex::checkIsAlive);// TODO move to system constraint?// add checkMeta ?
		components.stream().filter(x -> x != null).forEach(T::checkIsAlive);// TODO move to system constraint?

		List<T> supers = new ArrayList<>(new SupersComputer<>((T) getRoot(), meta, overrides, value, components));// TODO Order supers

		checkOverridesAreReached(overrides, supers);// TODO system constraints
		return newT(clazz, meta, supers, value, components);
	}

	// @Override
	// @SuppressWarnings("unchecked")
	// default T setMeta(int dim) {
	// T adjustedMeta = ((T) this).adjustMeta(dim);
	// if (adjustedMeta.getComponents().size() == dim)
	// return adjustedMeta;
	// List<T> components = new ArrayList<>();
	// for (int i = 0; i < dim; i++)
	// components.add((T) this);
	// List<T> supers = Collections.singletonList(adjustedMeta);
	// return ((T) this).rebuildAll(() -> ((T) this).newT(null, null, Collections.singletonList(adjustedMeta), getValue(), components).plug(), adjustedMeta.computePotentialDependencies(supers, getValue(), components));
	// }

	void checkOverridesAreReached(List<T> overrides, List<T> supers) {
		if (!Statics.areOverridesReached(overrides, supers))
			getRoot().discardWithException(new IllegalStateException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
	}

	static interface SingularsLazyCache {
		boolean get(int i);
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

	@SuppressWarnings("unchecked")
	protected boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || isSuperOf(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	protected boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents) {
		return isSuperOf(getMeta(), getValue(), getComponents(), superMeta, superValue, superComponents);
	}

	private static <T extends AbstractVertex<T>> boolean isSuperOf(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (subMeta == null) {
			if (!superMeta.isMeta())
				return false;
		} else if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!superMeta.componentsDepends(subComponents, superComponents))
			return false;
		if (superMeta.isPropertyConstraintEnabled())
			return !subComponents.equals(superComponents);
		return Objects.equals(subValue, superValue);

	}

	@Override
	public Snapshot<T> getComposites() {
		return getCompositesDependencies();
	}

	@SuppressWarnings("unchecked")
	protected <subT extends T> subT plug() {
		T result = this != getMeta() ? ((AbstractVertex<T>) getMeta()).indexInstance((T) this) : (T) this;
		getSupers().forEach(superGeneric -> ((AbstractVertex<T>) superGeneric).indexInheriting((T) this));
		getComponents().stream().filter(component -> !equals(component)).forEach(component -> ((AbstractVertex<T>) component).indexComposite((T) this));
		getRoot().check(true, false, (T) this);
		return (subT) result;
	}

	@SuppressWarnings("unchecked")
	protected boolean unplug() {
		getRoot().check(false, false, (T) this);
		boolean result = this != getMeta() ? ((AbstractVertex<T>) getMeta()).unIndexInstance((T) this) : true;
		if (!result)
			getRoot().discardWithException(new NotFoundException(this.info()));
		getSupers().forEach(superGeneric -> ((AbstractVertex<T>) superGeneric).unIndexInheriting((T) this));
		getComponents().stream().filter(component -> !equals(component)).forEach(component -> ((AbstractVertex<T>) component).unIndexComposite((T) this));
		return result;
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

	private T indexComposite(T composite) {
		return index(getCompositesDependencies(), composite);
	}

	private boolean unIndexInstance(T instance) {
		return unIndex(getInstancesDependencies(), instance);
	}

	private boolean unIndexInheriting(T inheriting) {
		return unIndex(getInheritingsDependencies(), inheriting);
	}

	private boolean unIndexComposite(T composite) {
		return unIndex(getCompositesDependencies(), composite);
	}

	@SuppressWarnings("unchecked")
	T getMap() {
		return getRoot().getMetaAttribute().getDirectInstance(SystemMap.class, Collections.singletonList((T) getRoot()));
	}

	public static class SystemMap {
	}

	Stream<T> getKeys() {
		T map = getMap();
		return map != null ? getAttributes(map).get() : Stream.empty();
	}

	Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> Objects.equals(x.getValue(), property)).findFirst();
	}

	Stream<T> getKeys(Class<?> propertyClass) {
		return getKeys().filter(x -> x.getValue() instanceof AxedPropertyClass && Objects.equals(((AxedPropertyClass) x.getValue()).getClazz(), propertyClass));
	}

	protected void checkSystemConstraints(boolean isOnAdd, boolean isFlushTime) {
		if (isMeta())
			checkMeta();
		if (!isFlushTime)
			checkIsAlive();
		else if (!isOnAdd && isAlive())
			getRoot().discardWithException(new AliveConstraintViolationException(info()));
		if (!isOnAdd)
			checkDependenciesAreEmpty();
		checkDependsMetaComponents();
		checkSupers();
		checkDependsSuperComponents();
		checkLevel();
		checkLevelComponents();

	}

	private void checkMeta() {
		if (!getComponents().stream().allMatch(c -> c.isRoot()) || !Objects.equals(getValue(), getRoot().getValue()) || getSupers().size() != 1 || !getSupers().get(0).isMeta())
			getRoot().discardWithException(new IllegalStateException("Malformed meta : " + info()));
	}

	private void checkDependenciesAreEmpty() {
		if (!getInstances().isEmpty() || !getInheritings().isEmpty() || !getComposites().isEmpty())
			getRoot().discardWithException(new ReferentialIntegrityConstraintViolationException("Unable to remove : " + info() + " cause it has dependencies"));
	}

	private void checkDependsMetaComponents() {
		if (getMeta().getComponents().size() != getComponents().size())
			getRoot().discardWithException(new MetaRuleConstraintViolationException("Added generic and its meta do not have the same components size. Added node components : " + getComponents() + " and meta components : " + getMeta().getComponents()));

		for (int pos = 0; pos < getComponents().size(); pos++)
			if (!getComponent(pos).isInstanceOf(getMeta().getComponent(pos)) && !getComponent(pos).inheritsFrom(getMeta().getComponent(pos)))
				getRoot().discardWithException(new MetaRuleConstraintViolationException("Component of added generic : " + getComponent(pos) + " must be instance of or must inherits from the component of its meta : " + getMeta().getComponent(pos)));
	}

	private void checkLevelComponents() {
		for (T component : getComponents())
			if (component.getLevel() > getLevel())
				getRoot().discardWithException(new MetaLevelConstraintViolationException("Inappropriate component meta level : " + component.getLevel() + " for component : " + component + ". Component meta level for added node is : " + getLevel()));
	}

	private void checkLevel() {
		if (getLevel() > Statics.CONCRETE)
			getRoot().discardWithException(new MetaLevelConstraintViolationException("Unable to instanciate a concrete generic : " + getMeta()));
	}

	private void checkSupers() {
		supers.forEach(AbstractVertex::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			getRoot().discardWithException(new IllegalStateException("Inconsistant supers (bad level) : " + supers));
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

	void checkConstraints(boolean isOnAdd, boolean isFlushTime) {
		if (getMap() != null) {
			Stream<T> contraintsHolders = getMeta().getHolders(getMap()).get().filter(holder -> holder.getMeta().getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) holder.getMeta().getValue()).getClazz()))
					.filter(holder -> holder.getValue() != null && !Boolean.FALSE.equals(holder.getValue())).sorted(CONSTRAINT_PRIORITY);
			contraintsHolders.forEach(constraintHolder -> {
				T baseComponent = constraintHolder.getBaseComponent();
				if (isSpecializationOf(baseComponent))
					check(constraintHolder, baseComponent, isFlushTime, isOnAdd, false);
				T targetComponent = constraintHolder.getTargetComponent();
				if (targetComponent != null && isSpecializationOf(targetComponent))
					check(constraintHolder, baseComponent, isFlushTime, isOnAdd, true);
			});
		}
	}

	@SuppressWarnings("unchecked")
	void check(T constraintHolder, T baseComponent, boolean isFlushTime, boolean isOnAdd, boolean isRevert) {
		try {
			constraintHolder.getMeta().statelessConstraint().check((T) this, baseComponent, constraintHolder.getValue(), ((AxedPropertyClass) constraintHolder.getMeta().getValue()).getAxe(), isOnAdd, isFlushTime, isRevert);
		} catch (ConstraintViolationException e) {
			getRoot().discardWithException(e);
		}
	}

	@SuppressWarnings("unchecked")
	Constraint<T> statelessConstraint() {
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

	void checkConsistency(boolean isOnAdd, boolean isFlushTime) {
		if (getMap() != null && getMeta().getValue() instanceof AxedPropertyClass && Constraint.class.isAssignableFrom(((AxedPropertyClass) getMeta().getValue()).getClazz()) && !Boolean.FALSE.equals(getMeta().getValue())) {
			T baseConstraint = getComponent(Statics.BASE_POSITION);
			baseConstraint.getAllInstances().forEach(x -> x.check((T) this, baseConstraint, isFlushTime, isOnAdd, false));
		}
	}

	private static final Comparator<AbstractVertex<?>> CONSTRAINT_PRIORITY = new Comparator<AbstractVertex<?>>() {
		@Override
		public int compare(AbstractVertex<?> constraintHolder, AbstractVertex<?> compareConstraintHolder) {
			return constraintHolder.getMeta().getConstraintPriority() < compareConstraintHolder.getMeta().getConstraintPriority() ? -1 : 1;
		}
	};

}
