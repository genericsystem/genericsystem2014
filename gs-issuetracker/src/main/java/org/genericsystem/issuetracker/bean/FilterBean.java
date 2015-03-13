package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class FilterBean {

	public List<Generic> filter(Generic issue, String searchedStatut, Predicate<? super Generic> predicate) {
		return (searchedStatut != null) ? (issue.getAllInstances().get().filter(predicate).collect(Collectors.toList())) : issue.getAllInstances().get().collect(Collectors.toList());
	}
}
