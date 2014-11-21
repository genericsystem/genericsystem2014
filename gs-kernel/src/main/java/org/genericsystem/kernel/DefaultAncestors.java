package org.genericsystem.kernel;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DefaultAncestors<T extends DefaultVertex<T>> extends IVertex<T> {

	static Logger log = LoggerFactory.getLogger(DefaultAncestors.class);

	@Override
	default boolean isRoot() {
		return this.equals(getRoot());
	}

	@SuppressWarnings("unchecked")
	@Override
	default boolean isAlive() {
		return equals(((T) this).getAlive());
	}

	@Override
	default void checkIsAlive() {
		if (!isAlive())
			getRoot().discardWithException(new AliveConstraintViolationException(info()));
	}

	@Override
	default int getLevel() {
		return this == getMeta() ? 0 : getMeta().getLevel() + 1;
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
	default boolean inheritsFrom(T superVertex) {
		if (equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return getSupers().stream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
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
	default boolean isCompositeOf(T vertex) {
		return isRoot() || getComponents().stream().anyMatch(component -> vertex.isSpecializationOf(component));
	}

	@Override
	default T getBaseComponent() {
		return getComponent(Statics.BASE_POSITION);
	}

	@Override
	default T getTargetComponent() {
		return getComponent(Statics.TARGET_POSITION);
	}

	@Override
	default T getTernaryComponent() {
		return getComponent(Statics.TERNARY_POSITION);
	}

	@Override
	default T getComponent(int pos) {
		return pos >= 0 && pos < getComponents().size() ? getComponents().get(pos) : null;
	}
}
