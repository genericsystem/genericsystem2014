package org.genericsystem.mutability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.concurrency.AbstractVertex;

public class MutabilityCache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends HashMap<M, T> {

	private static final long serialVersionUID = -3394154384323595664L;
	private final DefaultEngine<M, T, V> engine;
	private final Map<T, List<M>> reverseMap = new HashMap<>();

	public MutabilityCache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.Engine concurrencyEngine) {
		this.engine = engine;
		this.put((M) engine, (T) concurrencyEngine);

	}

	@Override
	public T get(Object key) {
		return super.get(key);
	}

	@Override
	public T put(M key, T value) {
		// if (((IVertex<?>) key).isMeta() && !((IVertex<?>) key).getComponents().isEmpty())
		// System.out.println("zzzzzzzzzzzzzzzzz generic " + ((IVertex<?>) key).info());
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
		List<M> reverseResult = reverseMap.get(generic);
		if (reverseResult == null) {
			M mutable = ((M) engine).newT().init(generic.isMeta() ? null : getByValue(generic.getMeta()), generic.getSupers().stream().map(this::getByValue).collect(Collectors.toList()), generic.getValue(),
					generic.getComponents().stream().map(this::getByValue).collect(Collectors.toList()));
			assert mutable != null;
			put(mutable, generic);
			return mutable;
		} else
			return reverseResult.get(0);
	}

}
