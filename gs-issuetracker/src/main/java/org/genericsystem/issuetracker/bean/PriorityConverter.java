package org.genericsystem.issuetracker.bean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.model.Priority;
import org.genericsystem.mutability.Generic;

@Named
public class PriorityConverter implements Converter {
	@Inject
	Engine engine;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		if (value != null) {
			Generic priority = engine.find(Priority.class);
			Generic searchedPriority = priority.getInstance(value);
			return searchedPriority;
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		if (value != null) {
			return (String) ((Generic)value).getValue();
		}
		return null;
	}

}
