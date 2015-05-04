package org.genericsystem.admin;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableCell;

import org.genericsystem.admin.App.LinksObservableList;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Generic;

public class LinksTableCell extends TableCell<Generic,Snapshot<Generic>>  {
	private final  LinksTableView linksTableView;
	
	public LinksTableCell(Generic attribute,int pos){
		this.linksTableView = new LinksTableView(attribute,pos);
		//setPrefHeight(linksTableView.getItems().size());
	    prefHeightProperty().bind(new SimpleIntegerProperty(112).multiply(Bindings.size(linksTableView.getItems()).add(1.01)));
	    minHeightProperty().bind(prefHeightProperty());
	    maxHeightProperty().bind(prefHeightProperty());
	}

	@Override
	protected void updateItem(Snapshot<Generic> links, boolean empty) {
		super.updateItem(links, empty);
		if(empty || links==null){
			linksTableView.setItems(null);
			setGraphic(null);
			setText(null);
		}else {
			linksTableView.setItems(new LinksObservableList(links));
			setGraphic(linksTableView);
		}
	};
}