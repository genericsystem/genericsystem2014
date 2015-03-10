package org.genericsystem.issuetracker.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.annotation.InjectedClass;
import org.genericsystem.issuetracker.model.Statut;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@SessionScoped
public class StatutBean implements Serializable {

	private static final long serialVersionUID = -7612611706483937791L;

	@Inject
	@Provide
	@InjectedClass(Statut.class)
	private transient Generic statut;
	private transient List<String> statuts;

	@PostConstruct
	private void init() {
		statuts = statut.getInstances().get().map(generic -> Objects.toString(generic.getValue())).collect(Collectors.toList());
	}

	public List<String> getStatuts() {
		return statuts;
	}

}
