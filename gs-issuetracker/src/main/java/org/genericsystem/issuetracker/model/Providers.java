package org.genericsystem.issuetracker.model;

import java.util.logging.Logger;

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
	private static final Logger log = Logger.getAnonymousLogger();

	@Inject
	private transient Engine engine;

	@Produces
	@Provide
	public Generic getGeneric(InjectionPoint ip) {
		InjectedClass annotation = ip.getAnnotated().getAnnotation(InjectedClass.class);
		log.info("Providers InjectedClass : " + annotation.value());
		if (annotation != null) {
			return engine.find(annotation.value());
		}

		// TODO ????
		return null;
	}
}
