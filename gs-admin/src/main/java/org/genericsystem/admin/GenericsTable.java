package org.genericsystem.admin;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.genericsystem.admin.AbstractColumn.GenericColumn;
import org.genericsystem.admin.AbstractColumn.TargetComponentColumn;
import org.genericsystem.mutability.Generic;


/**
 * @author Nicolas Feybesse
 *
 */
public class GenericsTable extends TableView<Generic>{
	public GenericsTable(Generic type,List<Generic> attributes) {
		setEditable(true);

		TableColumn<Generic, String> firstColumn = new GenericColumn<>(type, g -> Objects.toString(g.getValue()), (g, v) -> g.updateValue(v));
		getColumns().add(firstColumn);

		for (Generic attribute : attributes) {
			TableColumn<Generic, ?> column;
			if(attribute.getComponents().size()<2)
				column= new GenericColumn<>(attribute, g -> g.getValue(attribute), (g, v) -> g.setHolder(attribute, v));
			else
				column= new TargetComponentColumn(attribute.getTargetComponent(),
						g -> g.getHolder(attribute).getTargetComponent(),
						(g, v) -> g.setLink(attribute,null, v));
			getColumns().add(column);
		}
		getColumns().add(new DeleteColumn());


		ObservableList<Generic> data = FXCollections.observableArrayList(type.getInstances().stream().collect(Collectors.toList()));
		setItems(data);
	}
	
	public static class DeleteColumn extends TableColumn<Generic, Generic> {
		public DeleteColumn() {
			super("Delete");
			setMinWidth(200);
			setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
			setEditable(true);
			setCellFactory(column->new DeleteButtonCell());
		}
	}
	
	public static class DeleteButtonCell extends TableCell<Generic, Generic> {
		private final Button cellButton = new Button();

		public DeleteButtonCell(){
			cellButton.setMaxWidth(200);
			cellButton.setAlignment(Pos.BASELINE_CENTER);
		}

		@Override
		protected void updateItem(Generic t, boolean empty) {
			super.updateItem(t, empty);
			if(empty || t==null){
				cellButton.setText(null);
				setGraphic(null);
			}else {
				cellButton.setText("Delete");
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
}

