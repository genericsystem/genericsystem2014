package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SignatureService<T extends SignatureService<T>> {

	static Logger log = LoggerFactory.getLogger(SignatureService.class);

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

	default boolean equiv(SignatureService<? extends SignatureService<?>> service) {
		return service == null ? false : equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean equiv(SignatureService<?> meta, Serializable value, List<? extends SignatureService<?>> components) {
		return getMeta().equiv(meta) && Objects.equals(getValue(), value) && equivComponents(getComponents(), components);
	}

	static boolean equivComponents(List<? extends SignatureService<?>> components, List<? extends SignatureService<?>> otherComponents) {
		if (otherComponents.size() != components.size())
			return false;
		Iterator<? extends SignatureService<?>> otherComponentsIt = otherComponents.iterator();
		return components.stream().allMatch(x -> x.equiv(otherComponentsIt.next()));
	}

	default boolean weakEquiv(SignatureService<? extends SignatureService<?>> service) {
		return service == this ? true : weakEquiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean weakEquiv(SignatureService<?> meta, Serializable value, List<? extends SignatureService<?>> components) {
		return getMeta().weakEquiv(meta) && getMeta().getWeakPredicate().test(getValue(), getComponents(), value, components);
	}

	default WeakPredicate getWeakPredicate() {
		return (value, components, otherValue, otherComponents) -> singularOrReferential(components, otherComponents) || (weakEquiv(components, otherComponents) && getValuesBiPredicate().test(value, otherValue));
	}

	default boolean singularOrReferential(List<? extends SignatureService<?>> components, List<? extends SignatureService<?>> otherComponents) {
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

	public static BiPredicate<List<? extends SignatureService<?>>, List<? extends SignatureService<?>>> SIZE_EQUALS = (X, Y) -> {
		return X.size() == Y.size();
	};

	default boolean weakEquiv(List<? extends SignatureService<?>> components, List<? extends SignatureService<?>> otherComponents) {
		if (!(components.size() == otherComponents.size()))
			return false;
		Iterator<? extends SignatureService<?>> otherComponentsIt = otherComponents.iterator();
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
		boolean test(Serializable value, List<? extends SignatureService<?>> components, Serializable otherValue, List<? extends SignatureService<?>> otherComponents);
	}

}
