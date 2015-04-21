package org.genericsystem.examplejavafx;

import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.genericsystem.mutability.Generic;

public class GenericsTable extends TableView<Generic>{
	public GenericsTable(Generic type,Generic...attributes) {
		setEditable(true);

		TableColumn<Generic, String> firstColumn = new GenericColumn<>(type,Objects.toString(type.getValue()), g -> Objects.toString(g.getValue()), (g, v) -> g.updateValue(v));
		getColumns().add(firstColumn);

		for (Generic attribute : attributes) {
			TableColumn<Generic, ?> column;
			if(attribute.getComponents().size()<2)
				column= new GenericColumn<>(attribute,Objects.toString(attribute.getValue()),  g -> g.getValue(attribute), (g, v) -> g.setHolder(attribute, v));
			else
				column= new TargetGenericColumn(attribute.getTargetComponent(),Objects.toString(attribute.getTargetComponent().getValue()),
						g -> g.getHolder(attribute).getTargetComponent(),
						(g, v) -> g.setLink(attribute,null, v));
			getColumns().add(column);
		}

		TableColumn<Generic, Generic> lastColumn =new TableColumn<>("Delete");
		lastColumn.setMinWidth(200);
		lastColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
		lastColumn.setEditable(true);
		lastColumn.setCellFactory(column->new DeleteButtonCell());
		getColumns().add(lastColumn);


		ObservableList<Generic> data = FXCollections.observableArrayList(type.getInstances().stream().collect(Collectors.toList()));
		setItems(data);
	}
}

