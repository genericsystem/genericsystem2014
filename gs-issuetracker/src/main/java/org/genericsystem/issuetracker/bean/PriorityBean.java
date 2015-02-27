package org.genericsystem.issuetracker.bean;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.model.Priority;
import org.genericsystem.mutability.Generic;

@Named
@SessionScoped
public class PriorityBean implements Serializable {

	private static final long serialVersionUID = 3628359912273571503L;
	// private static final Logger log = Logger.getAnonymousLogger();

	@Inject
	private transient Engine engine;

	@PostConstruct
	public void init() {
		Generic priority = engine.find(Priority.class);
		priority.setInstance("Low");
		priority.setInstance("Normal");
		priority.setInstance("Hight");
		engine.getCurrentCache().flush();
	}

	public List<Generic> getPriorities() {
		return engine.find(Priority.class).getAllInstances().get().collect(Collectors.toList());
	}

}
