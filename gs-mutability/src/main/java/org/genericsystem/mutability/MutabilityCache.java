//package org.genericsystem.mutability;
//
//import java.util.HashMap;
//import java.util.IdentityHashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.genericsystem.concurrency.AbstractVertex;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class MutabilityCache<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends HashMap<M, T> {
//
//	private static Logger log = LoggerFactory.getLogger(MutabilityCache.class);
//	private static final long serialVersionUID = -3394154384323595664L;
//	private final DefaultEngine<M, T, V> engine;
//	private final org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine;
//
//	private final Map<T, IdentityHashMap<M, Boolean>> reverseMap = new HashMap<>();
//
//	public MutabilityCache(DefaultEngine<M, T, V> engine, org.genericsystem.concurrency.DefaultEngine<T, V> concurrencyEngine) {
//		this.engine = engine;
//		this.put((M) engine, (T) concurrencyEngine);
//		this.concurrencyEngine = concurrencyEngine;
//
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public T get(Object key) {
//		M mutable = (M) key;
//		T result = super.get(mutable);
//		if (result == null) {
//			if (mutable.isMeta()) {
//				T pluggedSuper = get(mutable.getSupers().get(0));
//				if (pluggedSuper != null) {
//					for (T inheriting : pluggedSuper.getInheritings())
//						if (mutable.equals(inheriting)) {
//							put(mutable, inheriting);
//							return inheriting;
//						}
//
//					result = pluggedSuper.setMeta(mutable.getComponents().size());
//					put(mutable, result);
//					return result;
//				}
//			} else {
//				T pluggedMeta = get(mutable.getMeta());
//				if (pluggedMeta != null) {
//					for (T instance : pluggedMeta.getInstances())
//						if (mutable.equals(instance)) {
//							put(mutable, instance);
//							return instance;
//						}
//					result = ((T) concurrencyEngine).newT().init(pluggedMeta, mutable.getSupers().stream().map(this::get).collect(Collectors.toList()), mutable.getValue(), mutable.getComponents().stream().map(this::get).collect(Collectors.toList()));
//					put(mutable, result);
//					return result;
//				}
//			}
//		}
//		return result;
//	}
//
//	@Override
//	public T put(M key, T value) {
//		IdentityHashMap<M, Boolean> reverseResult = reverseMap.get(value);
//		if (reverseResult == null) {
//			IdentityHashMap<M, Boolean> idHashMap = new IdentityHashMap<>();
//			idHashMap.put(key, true);
//			reverseMap.put(value, idHashMap);
//		} else
//			reverseResult.put(key, true);
//		return super.put(key, value);
//	}
//
//	public M getByValue(T generic) {
//		IdentityHashMap<M, Boolean> reverseResult = reverseMap.get(generic);
//		if (reverseResult == null) {
//			M mutable = ((M) engine).newT().init(generic.isMeta() ? null : getByValue(generic.getMeta()), generic.getSupers().stream().map(this::getByValue).collect(Collectors.toList()), generic.getValue(),
//					generic.getComponents().stream().map(this::getByValue).collect(Collectors.toList()));
//			assert mutable != null;
//			put(mutable, generic);
//			return mutable;
//		} else
//			return reverseResult.keySet().iterator().next();
//	}
// }
