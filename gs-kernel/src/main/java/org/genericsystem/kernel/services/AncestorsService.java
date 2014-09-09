package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.NotAliveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AncestorsService<T extends VertexService<T, U>, U extends RootService<T, U>> extends ApiService<T, U> {

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
	default T checkIsAlive() {
		T result = getAlive();
		if (!equals(getAlive()))
			getRoot().discardWithException(new NotAliveException(info()));
		return result;
	}

	@Override
	default T getAlive() {
		T pluggedMeta = getMeta().getAlive();
		if (pluggedMeta == null)
			return null;
		for (T instance : pluggedMeta.getInstances())
			if (equals(instance))
				return instance;
		return null;
	}

	@Override
	default boolean equals(ApiService<?, ?> meta, List<? extends ApiService<?, ?>> supers, Serializable value, List<? extends ApiService<?, ?>> components) {
		return (isRoot() || getMeta().equals(meta)) && Objects.equals(getValue(), value) && getComponents().equals(components) && getSupers().equals(supers);
	}

	@Override
	default boolean equalsAnySupers(ApiService<?, ?> meta, Serializable value, List<? extends ApiService<?, ?>> components) {
		return (isRoot() || getMeta().equals(meta)) && Objects.equals(getValue(), value) && getComponents().equals(components);
	}

	@Override
	default boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		return equals(service) ? true : equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	@Override
	default boolean equiv(ApiService<?, ?> meta, Serializable value, List<? extends ApiService<?, ?>> components) {
		if (!getMeta().equiv(meta))
			return false;
		if (getComponents().size() != components.size())
			return false;// for the moment, no weak equiv when component size is different
		for (int i = 0; i < getComponents().size(); i++)
			if (isReferentialIntegrityConstraintEnabled(i) && isSingularConstraintEnabled(i) && getComponents().get(i).equiv(components.get(i)))
				return true;
		for (int i = 0; i < getComponents().size(); i++)
			if (!getComponents().get(i).equiv(components.get(i)))
				return false;
		if (!meta.isPropertyConstraintEnabled())
			return Objects.equals(getValue(), value);
		return true;
	}

	@Override
	default int getLevel() {
		return isRoot() || getValue().equals(getRoot().getValue()) || getComponentsStream().allMatch(c -> c.isRoot()) ? 0 : getMeta().getLevel() + 1;
	}

	@Override
	default U getRoot() {
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
