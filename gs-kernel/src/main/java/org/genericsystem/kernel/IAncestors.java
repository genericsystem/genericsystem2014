package org.genericsystem.kernel;

import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.kernel.exceptions.NotAliveException;
import org.genericsystem.kernel.services.IVertexBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IAncestors<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	static Logger log = LoggerFactory.getLogger(IAncestors.class);

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

	// @Override
	// default boolean equalsRegardlessSupers(ApiService<?, ?> meta, Serializable value, List<? extends ApiService<?, ?>> components) {
	// return (isRoot() || getMeta().equals(meta)) && Objects.equals(getValue(), value) && getComponents().equals(components);
	// }

	@Override
	default boolean equiv(IVertexBase<? extends IVertexBase<?, ?>, ?> service) {
		return equals(service) ? true : ((AbstractVertex<?, ?>) this).equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	@Override
	default int getLevel() {
		return isRoot() || Objects.equals(getValue(), getRoot().getValue()) || getComponentsStream().allMatch(c -> c.isRoot()) ? 0 : getMeta().getLevel() + 1;
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
