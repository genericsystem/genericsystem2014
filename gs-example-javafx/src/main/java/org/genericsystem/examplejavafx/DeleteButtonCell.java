package org.genericsystem.examplejavafx;

import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
					t.remove();
					getTableView().getItems().remove(t);
					System.out.println("Remove : "+t.info());
				}
			});

		}
	}
}

