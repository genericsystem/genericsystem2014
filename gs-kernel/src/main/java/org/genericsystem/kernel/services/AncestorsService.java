package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.RollbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AncestorsService<T extends AncestorsService<T>> {

	static Logger log = LoggerFactory.getLogger(AncestorsService.class);

	T getMeta();

	Serializable getValue();

	List<T> getComponents();

	default Stream<T> getComponentsStream() {
		return getComponents().stream();
	}

	default boolean isRoot() {
		return false;
	}

	default boolean isAlive() {
		return equals(getAlive());
	}

	@SuppressWarnings("unchecked")
	default T getAlive() {
		T pluggedMeta = getMeta().getAlive();
		if (pluggedMeta == null)
			return null;
		for (T instance : (Snapshot<T>) (((DependenciesService<?>) pluggedMeta).getInstances()))
			if (equiv(instance))
				return instance;
		return null;
	}

	@SuppressWarnings("unchecked")
	default T getWeakAlive() {
		T pluggedMeta = getMeta().getAlive();
		if (pluggedMeta == null)
			return null;
		for (T instance : (Snapshot<T>) (((DependenciesService<?>) pluggedMeta).getInstances()))
			if (weakEquiv(instance))
				return instance;
		return null;
	}

	default boolean equiv(AncestorsService<? extends AncestorsService<?>> service) {
		return service == null ? false : equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean equiv(AncestorsService<?> meta, Serializable value, List<? extends AncestorsService<?>> components) {
		return getMeta().equiv(meta) && Objects.equals(getValue(), value) && equivComponents(getComponents(), components);
	}

	static boolean equivComponents(List<? extends AncestorsService<?>> components, List<? extends AncestorsService<?>> otherComponents) {
		if (otherComponents.size() != components.size())
			return false;
		Iterator<? extends AncestorsService<?>> otherComponentsIt = otherComponents.iterator();
		return components.stream().allMatch(x -> x.equiv(otherComponentsIt.next()));
	}

	default boolean weakEquiv(AncestorsService<? extends AncestorsService<?>> service) {
		return service == this ? true : weakEquiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean weakEquiv(AncestorsService<?> meta, Serializable value, List<? extends AncestorsService<?>> components) {
		return getMeta().weakEquiv(meta) && getMeta().getWeakPredicate().test(getValue(), getComponents(), value, components);
	}

	default WeakPredicate getWeakPredicate() {
		return (value, components, otherValue, otherComponents) -> singularOrReferential(components, otherComponents) || (weakEquiv(components, otherComponents) && getValuesBiPredicate().test(value, otherValue));
	}

	default boolean singularOrReferential(List<? extends AncestorsService<?>> components, List<? extends AncestorsService<?>> otherComponents) {
		if (!(components.size() == otherComponents.size()))
			return false;
		for (int i = 0; i < otherComponents.size(); ++i) {
			if (isReferentialIntegrityConstraintEnabled(i) && isSingularConstraintEnabled(i))
				return true;
		}
		return false;
	}

	public static BiPredicate<Serializable, Serializable> VALUE_EQUALS = (X, Y) -> Objects.equals(X, Y);
	public static BiPredicate<Serializable, Serializable> VALUE_IGNORED = (X, Y) -> true;
	public static BiPredicate<Serializable, Serializable> KEY_EQUALS = (X, Y) -> (X instanceof Entry) && (Y instanceof Entry) && Objects.equals(((Entry<?, ?>) X).getKey(), ((Entry<?, ?>) Y).getKey());

	public static BiPredicate<List<? extends AncestorsService<?>>, List<? extends AncestorsService<?>>> SIZE_EQUALS = (X, Y) -> {
		return X.size() == Y.size();
	};

	default boolean weakEquiv(List<? extends AncestorsService<?>> components, List<? extends AncestorsService<?>> otherComponents) {
		if (!(components.size() == otherComponents.size()))
			return false;
		Iterator<? extends AncestorsService<?>> otherComponentsIt = otherComponents.iterator();
		return components.stream().allMatch(x -> x.weakEquiv(otherComponentsIt.next()));
	}

	boolean isPropertyConstraintEnabled();

	boolean isReferentialIntegrityConstraintEnabled(int position);

	boolean isSingularConstraintEnabled(int position);

	default BiPredicate<Serializable, Serializable> getValuesBiPredicate() {
		return isPropertyConstraintEnabled() ? VALUE_IGNORED : VALUE_EQUALS.or(KEY_EQUALS);
	}

	@FunctionalInterface
	public interface WeakPredicate {
		boolean test(Serializable value, List<? extends AncestorsService<?>> components, Serializable otherValue, List<? extends AncestorsService<?>> otherComponents);
	}

	T[] coerceToArray(Object... array);

	void rollbackAndThrowException(Exception exception) throws RollbackException;

	default int getLevel() {
		return isRoot() || getValue().equals(getRoot().getValue()) || getComponentsStream().allMatch(c -> c.isRoot()) ? 0 : getMeta().getLevel() + 1;
	}

	default T getRoot() {
		return getMeta().getRoot();
	}

	default boolean isMeta() {
		return getLevel() == Statics.META;
	}

	default boolean isStructural() {
		return getLevel() == Statics.STRUCTURAL;
	}

	default boolean isConcrete() {
		return getLevel() == Statics.CONCRETE;
	}

	List<T> getSupers();

	default Stream<T> getSupersStream() {
		return getSupers().stream();
	}

	default boolean inheritsFrom(T superVertex) {
		if (equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return getSupersStream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
	}

	default boolean isInstanceOf(T metaVertex) {
		return getMeta().inheritsFrom(metaVertex);
	}

	default boolean isSpecializationOf(T supra) {
		return getLevel() == supra.getLevel() ? inheritsFrom(supra) : (getLevel() > supra.getLevel() && getMeta().isSpecializationOf(supra));
	}

	default boolean isAttributeOf(T vertex) {
		return isRoot() || getComponentsStream().anyMatch(component -> vertex.isSpecializationOf(component));
	}

	// default List<T> getUnreachedSupers(T instance, List<T> overrides) {
	// return overrides.stream().filter(override -> instance.getSupers().stream().allMatch(superVertex -> !superVertex.inheritsFrom(override))).collect(Collectors.toList());
	// }
	// default boolean hasSuperSameMeta() {
	// return getSupersStream().anyMatch(x -> getMeta().equals(x.getMeta()));
	// }

	// default Stream<T> getSuprasStream() {
	// return isRoot() || hasSuperSameMeta() ? getSupersStream() : Stream.concat(Stream.of(getMeta()), getSupersStream());
	// }
}