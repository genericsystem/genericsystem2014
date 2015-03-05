package org.genericsystem.issuetracker.model;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.annotation.InjectedClass;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@ApplicationScoped
public class Providers {

	@Inject
	private transient Engine engine;

	@Produces
	@Provide
	public Generic getGeneric(InjectionPoint ip) {
		InjectedClass annotation = ip.getAnnotated().getAnnotation(InjectedClass.class);
		if (annotation != null)
			return engine.find(annotation.value());

		// TODO ????
		return null;
	}
}
