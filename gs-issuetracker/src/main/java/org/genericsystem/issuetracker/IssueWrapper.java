package org.genericsystem.issuetracker;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "issues")
public class IssueWrapper {

	private List<Issue> list;

	public List<Issue> getList() {
		return list;
	}

	public void setList(List<Issue> list) {
		this.list = list;
	}

}
