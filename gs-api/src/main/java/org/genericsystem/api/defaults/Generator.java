package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.Objects;

public interface Generator<T extends DefaultVertex<T>> {

	Serializable generate(T type);

	public static class IntAutoIncrementGenerator<T extends DefaultVertex<T>> implements Generator<T> {

		@Override
		public Serializable generate(T type) {
			return getIncrementedValue(type);
		}

		protected int getIncrementedValue(T type) {
			T sequence = type.getRoot().getSequence();
			T sequenceHolder = null;
			if (type.getHolders(sequence).first() != null)
				sequenceHolder = type.getHolders(sequence).first();
			int value = sequenceHolder != null ? (Integer) sequenceHolder.getValue() + 1 : 0;
			type.setHolder(sequence, value);
			return value;

		}

		public static class StringAutoIncrementGenerator<T extends DefaultVertex<T>> extends IntAutoIncrementGenerator<T> {
			@Override
			public Serializable generate(T type) {
				Serializable value = getIncrementedValue(type);
				return type.getValue() instanceof Class<?> ? ((Class<?>) type.getValue()).getSimpleName() + "-" + value : Objects.toString(type.getValue() + "-" + value);
			}
		}
	}
}
