package org.genericsystem.mutability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.concurrency.AbstractVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutabilityCache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends HashMap<M, T> {

	private static Logger log = LoggerFactory.getLogger(MutabilityCache.class);
	private static final long serialVersionUID = -3394154384323595664L;
	private final DefaultEngine<M, T, V> engine;
	private final org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine;

	private final Map<T, List<M>> reverseMap = new HashMap<>();

	public MutabilityCache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine) {
		this.engine = engine;
		this.put((M) engine, (T) concurrencyEngine);
		this.concurrencyEngine = concurrencyEngine;

	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Object key) {
		M mutable = (M) key;
		T result = super.get(mutable);
		if (result == null) {
			if (mutable.isMeta()) {
				T pluggedSuper = get(mutable.getSupers().get(0));
				if (pluggedSuper != null) {
					for (T inheriting : pluggedSuper.getInheritings())
						if (mutable.equals(inheriting)) {
							put(mutable, inheriting);
							return inheriting;
						}

					result = pluggedSuper.setMeta(mutable.getComponents().size());// .init(null, mutable.getSupers().stream().map(this::get).collect(Collectors.toList()), mutable.getValue(),
																					// mutable.getComponents().stream().map(this::get).collect(Collectors.toList())).plug();
					put(mutable, result);
					return result;
				}
			} else {
				T pluggedMeta = get(mutable.getMeta());
				if (pluggedMeta != null) {
					for (T instance : pluggedMeta.getInstances())
						if (mutable.equals(instance)) {
							put(mutable, instance);
							return instance;
						}
					// mutable.getComponents().stream().map(this::get).peek(x -> log.info("" + x)).forEach(T::checkIsAlive);
					result = ((T) concurrencyEngine).newT().init(pluggedMeta, mutable.getSupers().stream().map(this::get).collect(Collectors.toList()), mutable.getValue(), mutable.getComponents().stream().map(this::get).collect(Collectors.toList()))
							.plug();
					put(mutable, result);
					return result;
				}
			}
		}
		return result;
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
