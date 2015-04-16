package org.genericsystem.examplejavafx;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.genericsystem.examplejavafx.model.Color;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class App extends Application{

	private final TableView<Generic> table = new TableView<>();
	private final Engine engine = new Engine(Color.class);
	private final Generic type = engine.find(Color.class);
	private final ObservableList<Generic> data =
			FXCollections.observableArrayList(
					type.getInstances().get().collect(Collectors.toList())
					);


	public static void main(String args[]){
		launch(args);
	}

	@Override
	public void start(Stage stage){
		Scene scene = new Scene(new Group());
		stage.setTitle("Generic System JavaFx Example");
		stage.setWidth(500);
		stage.setHeight(500);

		table.setEditable(true);
		TableColumn<Generic,String> firstColumn = buildColumn(Objects.toString(type.getValue()), 200, g->Objects.toString(g.getValue()), (g,v)->g.updateValue(v));
		TableColumn<Generic,String> secondColumn = buildColumn("Delete", 200,g-> "Delete "+Objects.toString(g.getValue()),null);

		table.setItems(data);
		table.getColumns().addAll(firstColumn, secondColumn);

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(table);

		((Group) scene.getRoot()).getChildren().addAll(vbox);

		stage.setScene(scene);
		stage.show();
	}


	TableColumn<Generic,String> buildColumn(String columnName,int minWidth,Function<Generic,String> getter,BiConsumer<Generic,String> setter){
		TableColumn<Generic,String> column = new TableColumn<Generic,String>(columnName);
		column.setMinWidth(minWidth);
		column.setCellValueFactory(cellData-> new ReadOnlyObjectWrapper<String>(getter.apply((cellData.getValue()))));
		column.setOnEditCommit((CellEditEvent<Generic, String> t) -> { setter.accept(((Generic) t.getTableView().getItems().get(t.getTablePosition().getRow())),t.getNewValue());});
		column.setEditable(true);
		return column;
	}
}
