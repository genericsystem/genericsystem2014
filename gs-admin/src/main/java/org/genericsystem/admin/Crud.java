package org.genericsystem.admin;


import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.genericsystem.mutability.Generic;

/**
 * @author Nicolas Feybesse
 *
 */
public class Crud extends VBox {

	public Crud(Generic type) {
		//setSpacing(5);
		setPadding(new Insets(10, 10, 10, 10));
		TableView<Generic> table = new InstancesTableView(type);
		HBox hb = new HBox();
		//hb.setSpacing(5);
		hb.setPadding(new Insets(10, 0, 0, 0));

		final TextField newTextFild = new TextField();
		newTextFild.setMaxWidth(200);
		final Button addButton = new Button("Add",new ImageView(new Image(getClass().getResourceAsStream("not.png"))));
		addButton.setPadding(new Insets(4, 4, 4, 4));
		
		addButton.setOnAction((ActionEvent e) -> {
			Generic generic = type.setInstance(newTextFild.getText());
			if(!table.getItems().contains(generic))
				table.getItems().add(generic);
		});	
		hb.getChildren().addAll(newTextFild,addButton);

		getChildren().addAll(table,hb);
	}
}
