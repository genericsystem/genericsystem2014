package org.genericsystem.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import org.genericsystem.admin.AbstractColumn.CheckBoxColumn;
import org.genericsystem.admin.AbstractColumn.EditColumn;
import org.genericsystem.admin.AbstractColumn.GenericStringConverter;
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
		//getColumns().add(new DeleteColumn(attribute));
		setEditable(true);
		widthProperty().addListener(new ChangeListener<Number>() {
	        @Override
	        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
	            // Get the table header
	            Pane header = (Pane)lookup("TableHeaderRow");
	            if(header!=null && header.isVisible()) {
	              header.setMaxHeight(4);
	              header.setMinHeight(4);
	              header.setPrefHeight(4);
	              header.setVisible(true);
	              header.setManaged(true);
	            }
	        }
	    });
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		
		setRowFactory(new Callback<TableView<Generic>, TableRow<Generic>>() {
			  @Override
			  public TableRow<Generic> call(TableView<Generic> tableView) {
			    final TableRow<Generic> row = new TableRow<>();
			    final ContextMenu rowMenu = new ContextMenu();
			    final ContextMenu tableMenu = tableView.getContextMenu();
			    if (tableMenu != null) {
			    	tableMenu.getItems().forEach(item->{
			    		MenuItem newItem = new MenuItem(item.getText(),item.getGraphic());
			    		newItem.onActionProperty().bind(item.onActionProperty());
			    		rowMenu.getItems().add(newItem);
			    		});
			    	//rowMenu.getItems().add(new SeparatorMenuItem());
			    }
			 
				
				final MenuItem deleteItem = new MenuItem("Delete : "+ new GenericStringConverter<>(attribute).toString(row.getItem()),new ImageView(new Image(getClass().getResourceAsStream("not.png"))));
				deleteItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Confirmation Dialog");
						alert.setHeaderText("Confirmation is required");
						alert.setContentText("Delete : "+ new GenericStringConverter<>(attribute).toString(getSelectionModel().getSelectedItem()) +" ?");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK)
							getItems().remove(getSelectionModel().getSelectedItem());
					}
				});
				// disable this menu item if nothing is selected:
				deleteItem.disableProperty().bind(
						Bindings.isEmpty(getSelectionModel().getSelectedItems()));
				deleteItem.textProperty().bind(Bindings.createStringBinding(()->"Delete : "+new GenericStringConverter<>(attribute).toString(row.getItem()),row.itemProperty()));
				rowMenu.getItems().addAll(deleteItem);

			    // only display context menu for non-null items:
			    row.contextMenuProperty().bind(
			      Bindings.when(Bindings.isNotNull(row.itemProperty()))
			      .then(rowMenu)
			      .otherwise((ContextMenu)null));
			    return row;
			  }
			});
	}



}