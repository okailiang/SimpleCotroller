package com.model;

import java.util.Set;

public class Action {

	private String name;

	private Set<Result> results;

	private ActionClass cla;

	public ActionClass getCla() {
		return cla;
	}

	public void setCla(ActionClass cla) {
		this.cla = cla;
	}

	public Action() {
	}

	public Action(String name, ActionClass cla, Set<Result> results) {
		this.name = name;
		this.cla = cla;
		this.results = results;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Result> getResults() {
		return results;
	}

	public void setResults(Set<Result> results) {
		this.results = results;
	}

}
