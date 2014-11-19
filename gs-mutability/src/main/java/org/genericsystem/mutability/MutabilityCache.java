package org.genericsystem.mutability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.concurrency.AbstractVertex;

public class MutabilityCache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends HashMap<M, T> {

	private static final long serialVersionUID = -3394154384323595664L;
	private final DefaultEngine<M, T, V> engine;
	private final Map<T, List<M>> reverseMap = new HashMap<>();

	public MutabilityCache(DefaultEngine<M, T, V> engine) {
		this.engine = engine;
	}

	@Override
	public T get(Object key) {
		if (key.equals(engine))
			return (T) engine.unwrap();
		return super.get(key);
	}

	@Override
	public T put(M key, T value) {
		// if (((IVertex<?>) key).isMeta() && !((IVertex<?>) key).getComponents().isEmpty())
		System.out.println("zzzzzzzzzzzzzzzzz generic " + ((IVertex<?>) key).info());
		List<M> reverseResult = reverseMap.get(value);
		if (reverseResult == null) {
			List<M> mList = new ArrayList<>();
			mList.add(key);
			reverseMap.put(value, mList);
		} else
			reverseResult.add(key);
		return super.put(key, value);
	}

	M getByValue(T generic) {
		System.out.println("generic " + generic.info());
		if (generic.equals(engine.unwrap()))
			return (M) engine;
		List<M> reverseResult = reverseMap.get(generic);
		System.out.println("reverseResult " + reverseResult);
		if (reverseResult == null) {
			T init = generic.newT().init(generic.getMeta(), generic.getSupers(), generic.getValue(), generic.getComposites().get().collect(Collectors.toList()));
			M mutable = null;
			// assert mutable != null;
			put(mutable, generic);
			return mutable;
		} else
			return reverseResult.get(0);
	}
}
