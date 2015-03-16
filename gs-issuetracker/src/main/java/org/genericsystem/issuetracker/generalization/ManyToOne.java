package org.genericsystem.issuetracker.generalization;

import java.util.List;

import javax.annotation.PostConstruct;

public abstract class ManyToOne {

	@PostConstruct
	protected abstract void initPriorities();

	public abstract List<String> getPriorities();

}
