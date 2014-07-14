package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.RootService;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.VertexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AncestorsService<T extends VertexService<T>> extends ApiService<T> {

	static Logger log = LoggerFactory.getLogger(AncestorsService.class);

	@Override
	default Stream<T> getComponentsStream() {
		return getComponents().stream();
	}

	@Override
	default boolean isRoot() {
		return false;
	}

	@Override
	default boolean isAlive() {
		return equals(getAlive());
	}

	@Override
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

	@Override
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

	@Override
	default boolean equiv(ApiService<? extends ApiService<?>> service) {
		return service == null ? false : equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	@Override
	default boolean equiv(ApiService<?> meta, Serializable value, List<? extends ApiService<?>> components) {
		return getMeta().equiv(meta) && Objects.equals(getValue(), value) && equivComponents(getComponents(), components);
	}

	static boolean equivComponents(List<? extends ApiService<?>> components, List<? extends ApiService<?>> otherComponents) {
		if (otherComponents.size() != components.size())
			return false;
		Iterator<? extends ApiService<?>> otherComponentsIt = otherComponents.iterator();
		return components.stream().allMatch(x -> x.equiv(otherComponentsIt.next()));
	}

	@Override
	default boolean weakEquiv(ApiService<? extends ApiService<?>> service) {
		return service == this ? true : weakEquiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	@Override
	default boolean weakEquiv(ApiService<?> meta, Serializable value, List<? extends ApiService<?>> components) {
		return getMeta().weakEquiv(meta) && getMeta().getWeakPredicate().test(getValue(), getComponents(), value, components);
	}

	@Override
	default WeakPredicate getWeakPredicate() {
		return (value, components, otherValue, otherComponents) -> singularOrReferential(components, otherComponents) || (weakEquiv(components, otherComponents) && getValuesBiPredicate().test(value, otherValue));
	}

	@Override
	default boolean singularOrReferential(List<? extends ApiService<?>> components, List<? extends ApiService<?>> otherComponents) {
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

	@Override
	default boolean weakEquiv(List<? extends ApiService<?>> components, List<? extends ApiService<?>> otherComponents) {
		if (!(components.size() == otherComponents.size()))
			return false;
		Iterator<? extends ApiService<?>> otherComponentsIt = otherComponents.iterator();
		return components.stream().allMatch(x -> x.weakEquiv(otherComponentsIt.next()));
	}

	@Override
	default BiPredicate<Serializable, Serializable> getValuesBiPredicate() {
		return isPropertyConstraintEnabled() ? VALUE_IGNORED : VALUE_EQUALS.or(KEY_EQUALS);
	}

	@Override
	default int getLevel() {
		return isRoot() || getValue().equals(getRoot().getValue()) || getComponentsStream().allMatch(c -> c.isRoot()) ? 0 : getMeta().getLevel() + 1;
	}

	@Override
	default RootService<T> getRoot() {
		return getMeta().getRoot();
	}

	@Override
	default boolean isMeta() {
		return getLevel() == Statics.META;
	}

	@Override
	default boolean isStructural() {
		return getLevel() == Statics.STRUCTURAL;
	}

	@Override
	default boolean isConcrete() {
		return getLevel() == Statics.CONCRETE;
	}

	@Override
	default Stream<T> getSupersStream() {
		return getSupers().stream();
	}

	@Override
	default boolean inheritsFrom(T superVertex) {
		if (equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return getSupersStream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
	}

	@Override
	default boolean isInstanceOf(T metaVertex) {
		return getMeta().inheritsFrom(metaVertex);
	}

	@Override
	default boolean isSpecializationOf(T supra) {
		return getLevel() == supra.getLevel() ? inheritsFrom(supra) : (getLevel() > supra.getLevel() && getMeta().isSpecializationOf(supra));
	}

	@Override
	default boolean isAttributeOf(T vertex) {
		return isRoot() || getComponentsStream().anyMatch(component -> vertex.isSpecializationOf(component));
	}
}