package org.genericsystem.kernel.services;


public interface BindingService<T extends VertexService<T, U>, U extends RootService<T, U>> extends ApiService<T, U> {

	// @Override
	// default T adjustMeta(List<T> overrides, Serializable subValue, @SuppressWarnings("unchecked") T... subComponents) {
	// return adjustMeta(overrides, subValue, Arrays.asList(subComponents));
	// }
	//
	// @SuppressWarnings("unchecked")
	// default T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents) {
	// T result = null;
	// for (T directInheriting : getInheritings()) {
	// if (directInheriting.equals(this, subValue, subComponents))
	// return (T) this;
	// if (isSpecializationOf(getMeta()) && ((AbstractVertex<T, U>) this).componentsDepends(subComponents, directInheriting.getComponents()))
	// if (result == null)
	// result = directInheriting;
	// else
	// getRoot().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
	// }
	// return result == null ? (T) this : result.adjustMeta(overrides, subValue, subComponents);
	// }

	// @Override
	// @SuppressWarnings("unchecked")
	// default T getInstance(Serializable value, T... components) {
	// T meta = getAlive();
	// if (meta == null)
	// return null;
	// meta = ((AbstractVertex<T, U>) this).adjustMeta(Collections.<T> emptyList(), value, components);
	// if (meta != this)
	// return meta.getInstance(value, components);
	// for (T instance : meta.getInstances())
	// if (instance.equals(meta, value, Arrays.asList(components)))
	// return instance;
	// return null;
	// }
	//
	// @Override
	// @SuppressWarnings("unchecked")
	// default T getInstance(List<T> supers, Serializable value, T... components) {
	// T result = getInstance(value, components);
	// return result != null && supers.stream().allMatch(superT -> result.inheritsFrom(superT)) ? result : null;
	// }
	//
	// @Override
	// default T getEquivInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
	// return getEquivInstance(value, Arrays.asList(components));
	// }
	//
	// @SuppressWarnings("unchecked")
	// default T getEquivInstance(Serializable value, List<T> components) {
	// T meta = getAlive();
	// if (meta == null)
	// return null;
	// meta = ((AbstractVertex<T, U>) this).adjustMeta(Collections.<T> emptyList(), value, components);
	// if (meta != this)
	// return meta.getEquivInstance(value, components);
	// for (T instance : meta.getInstances())
	// if (instance.equiv(meta, value, components))
	// return instance;
	// return null;
	// }
}
