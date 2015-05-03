package org.genericsystem.admin;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import org.genericsystem.admin.AbstractColumn.CheckBoxColumn;
import org.genericsystem.admin.AbstractColumn.DeleteColumn;
import org.genericsystem.admin.AbstractColumn.EditColumn;
import org.genericsystem.admin.AbstractColumn.TargetComponentColumn;
import org.genericsystem.mutability.Generic;

public class LinksTableView extends TableView<Generic> {


	public LinksTableView(Generic attribute,int axe) {
		if(Boolean.class.equals(attribute.getClassConstraint()))
			getColumns().add(new CheckBoxColumn(attribute, link -> (Boolean)link.getValue(), (link, target) -> {link.updateValue(target);}));
		else
			getColumns().add(new EditColumn<>(attribute, link -> link.getValue(), (link, target) -> {link.updateValue(target);}));

		for (int i=0;i<attribute.getComponents().size(); i++) {
			if(i!=axe){
				final int pos = i;
				getColumns().add(new TargetComponentColumn(attribute.getComponent(i),link -> link.getComponent(pos),(link, target) -> {
					List<Generic> components = new ArrayList<>(link.getComponents());
					components.set(pos,target);
					Generic base = components.remove(0);
					base.setHolder(attribute,link.getValue(), components.toArray(new Generic[components.size()]));
				}));
			}
		}
		getColumns().add(new DeleteColumn(attribute));
		setEditable(true);
		widthProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
	            // Get the table header
	            Pane header = (Pane)lookup("TableHeaderRow");
	            if(header!=null && header.isVisible()) {
	              header.setMaxHeight(0);
	              header.setMinHeight(0);
	              header.setPrefHeight(0);
	              header.setVisible(false);
	              header.setManaged(false);
	            }
	        }
	    });
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
	}



}