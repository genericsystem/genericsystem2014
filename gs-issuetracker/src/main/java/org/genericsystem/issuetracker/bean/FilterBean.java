package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class FilterBean {

	private Predicate<? super Generic> predicate;

	@Inject
	private StatutBean statutBean;

	private String searchedStatut;
	private List<String> statuts;

	public Predicate<? super Generic> getPredicate(Generic relation) {
		predicate = (searchedStatut != null) ? generic -> generic.getLinks(relation).get().anyMatch(link -> link.getTargetComponent().getValue().equals(searchedStatut)) : null;
		return predicate;
	}

	public String getSearchedStatut() {
		return searchedStatut;
	}

	public void setSearchedStatut(String searchedStatut) {
		this.searchedStatut = searchedStatut;
	}

	public List<String> getStatuts() {
		statuts = statutBean.getStatuts();
		return statuts;
	}

}
