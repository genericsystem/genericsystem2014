package org.genericsystem.examplejavafx;

import java.util.Objects;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;

import org.genericsystem.mutability.Generic;

public class DeleteButtonCell extends TableCell<Generic, Generic> {
	private final Button cellButton = new Button();

	public DeleteButtonCell(){
		cellButton.setMaxWidth(200);
		cellButton.setAlignment(Pos.BASELINE_LEFT);
	}

	@Override
	protected void updateItem(Generic t, boolean empty) {
		super.updateItem(t, empty);
		if(empty || t==null){
			cellButton.setText(null);
			setGraphic(null);
		}else {
			cellButton.setText("Delete : "+Objects.toString(t.getValue()));
			setGraphic(cellButton);
			cellButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent event) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmation Dialog");
					alert.setHeaderText("Confirmation is required");
					alert.setContentText("Are you sure you want to delete : "+ t.info()+" ?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK){
						t.remove();
						getTableView().getItems().remove(t);
						System.out.println("Remove : "+t.info());
					}
				}
			});

		}
	}
}

