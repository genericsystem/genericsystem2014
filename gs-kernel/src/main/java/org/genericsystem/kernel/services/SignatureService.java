package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SignatureService<T extends SignatureService<T>> {

	static Logger log = LoggerFactory.getLogger(SignatureService.class);

	T getMeta();

	List<T> getComponents();

	default Stream<T> getComponentsStream() {
		return getComponents().stream();
	}

	abstract Serializable getValue();

	T getAlive();

}
