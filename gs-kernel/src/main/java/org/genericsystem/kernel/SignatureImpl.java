package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SignatureImpl<T extends AbstractVertex<T, ?>> implements Supplier<T> {

	private final SignatureImpl<T> metaSignature;
	private List<Serializable> superValues;
	private Serializable value;
	private List<Serializable> compositeValues;

	public SignatureImpl(SignatureImpl<T> metaSignature) {
		this.metaSignature = metaSignature;
	}

	public Serializable getMetaValue() {
		return metaSignature.getValue();
	}

	public List<Serializable> getSuperValues() {
		return superValues;
	}

	public void setSuperValues(List<Serializable> superValues) {
		this.superValues = superValues;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	public List<Serializable> getCompositeValues() {
		return compositeValues;
	}

	public void setCompositeValues(List<Serializable> compositeValues) {
		this.compositeValues = compositeValues;
	}

	@Override
	public T get() {
		T meta = metaSignature.get();
		for (T instance : meta.getInstances())
			if (Objects.equals(value, instance.getValue()) && compositeValues.equals(instance.getComposites().stream().map(composite -> composite.getValue()).collect(Collectors.toList())))
				return instance;// KK What to do with supers ?
		return null;
	}

}
