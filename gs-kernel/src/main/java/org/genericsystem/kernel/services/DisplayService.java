package org.genericsystem.kernel.services;

import java.util.stream.Collectors;

public interface DisplayService<T extends DisplayService<T>> extends AncestorsService<T> {

	default String info() {
		return "(" + getMeta().getValue() + "){" + this + "}" + getSupersStream().map(x -> x.toString()).collect(Collectors.toList()) + getComponentsStream().map(x -> x.toString()).collect(Collectors.toList()) + " ";
	}

	default String inheritingsInfo() {
		return "{" + this + System.identityHashCode(this) + "}" + " Inheritings :" + ((InheritanceService) this).getInheritings().stream().map(x -> x.toString() + System.identityHashCode(x)).collect(Collectors.toList());
	}

	default String specializationsInfo() {
		return "{" + this + System.identityHashCode(this) + "}" + " Specialization :" + ((InheritanceService) this).getSpecializations().stream().map(x -> x.toString() + System.identityHashCode(x)).collect(Collectors.toList());
	}

	default String instancesInfo() {
		return "{" + this + System.identityHashCode(this) + "}" + " Instances :" + ((InheritanceService) this).getInstances().stream().map(x -> x.toString() + System.identityHashCode(x)).collect(Collectors.toList());
	}

	default String suprasInfo() {
		return "{" + this + System.identityHashCode(this) + "}" + " Supras :" + ((InheritanceService) this).getSupras().map(x -> x.toString() + System.identityHashCode(x)).collect(Collectors.toList());
	}

	default String allInfo() {
		return info() + "\n" + instancesInfo() + "\n" + inheritingsInfo() + "\n" + specializationsInfo() + "\n" + suprasInfo();
	}

	default void log() {
		log.info(info());
	}

	default void log(String prefix) {
		log.info(prefix + info());
	}
}
