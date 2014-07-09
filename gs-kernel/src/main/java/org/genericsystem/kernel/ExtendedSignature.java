package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.BindingService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.ExceptionAdviserService;

import org.genericsystem.kernel.services.MapService.SystemMap;

public abstract class ExtendedSignature<T extends ExtendedSignature<T>> extends Signature<T> {

	protected List<T> supers;

	@SuppressWarnings("unchecked")
	public T init(T meta, List<T> supers, Serializable value, List<T> components) {
		super.init(meta, value, components);
		this.supers = supers;
		checkDependsMetaComponents();
		checkSupers(supers);
		checkDependsSuperComponents(supers);
		return (T) this;
	}

	private void checkDependsMetaComponents() {
		if (getValue().toString().equals(SystemMap.class.toString()) || getValue().toString().contains("PropertyConstraint"))
			return;
		assert getMeta().getComponents() != null;
		if (!(((BindingService) getMeta()).componentsDepends(getComponents(), getMeta().getComponents())))
			((ExceptionAdviserService) this).rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponents() + " " + getMeta().getComponents()));
	}

	private void checkSupers(List<T> supers) {
		supers.forEach(Signature::checkIsAlive);
		if (!supers.stream().allMatch(superVertex -> superVertex.getLevel() == getLevel()))
			((ExceptionAdviserService) this).rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupers()));
		if (!supers.stream().allMatch(superVertex -> ((AncestorsService) getMeta()).inheritsFrom((AncestorsService) superVertex.getMeta())))
			((ExceptionAdviserService) this).rollbackAndThrowException(new IllegalStateException("Inconsistant supers : " + getSupers()));
		if (!supers.stream().noneMatch(this::equals))
			((ExceptionAdviserService) this).rollbackAndThrowException(new IllegalStateException("Supers loop detected : " + ((DisplayService) this).info()));
	}

	private void checkDependsSuperComponents(List<T> supers) {
		((AncestorsService) this).getSupersStream().forEach(superVertex -> {
			if (!((BindingService) superVertex).isSuperOf((BindingService) getMeta(), (List) supers, getValue(), (List) getComponents()))
				((ExceptionAdviserService) this).rollbackAndThrowException(new IllegalStateException("Inconsistant components : " + getComponentsStream().collect(Collectors.toList())));
		});
	}

	public List<T> getSupers() {
		return supers;
	}

}
