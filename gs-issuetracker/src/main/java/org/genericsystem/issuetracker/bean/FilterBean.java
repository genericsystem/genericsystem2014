package org.genericsystem.issuetracker.bean;

import java.util.function.Predicate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class FilterBean {

	private Predicate<? super Generic> predicate;

	public Predicate<? super Generic> getPredicate(Generic relation, String searchedGeneric) {
		predicate = generic -> generic.getLinks(relation).get().anyMatch(link -> link.getTargetComponent().getValue().equals(searchedGeneric));
		return predicate;
	}

}
