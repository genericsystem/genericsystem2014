package org.genericsystem.kernel.services;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DisplayService<T extends AncestorsService<T>> extends AncestorsService<T> {
	static Logger log = LoggerFactory.getLogger(DisplayService.class);

	default String info() {
		return "(" + getMeta().getValue() + "){" + this + "}" + getSupersStream().collect(Collectors.toList()) + getComponentsStream().collect(Collectors.toList()) + " ";
	}

	default void log() {
		log.info(info());
	}
}
