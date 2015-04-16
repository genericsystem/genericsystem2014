package org.genericsystem.examplejavafx;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.genericsystem.examplejavafx.model.Car;
import org.genericsystem.examplejavafx.model.CarColor;
import org.genericsystem.examplejavafx.model.Color;
import org.genericsystem.examplejavafx.model.Power;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class App extends Application {

	private final TableView<Generic> table = new TableView<>();
	private final Engine engine = new Engine(Car.class, Power.class, CarColor.class, Color.class);

	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Generic type = engine.find(Car.class);
		Generic[] attributes = new Generic[] { engine.find(Power.class), engine.find(CarColor.class) };

		Scene scene = new Scene(new Group());
		stage.setTitle("Generic System JavaFx Example");
		stage.setWidth(500);
		stage.setHeight(500);

		table.setEditable(true);
		TableColumn<Generic, String> firstColumn = buildColumn(Objects.toString(type.getValue()), 200, g -> Objects.toString(g.getValue()), (g, v) -> g.updateValue(v));
		table.getColumns().add(firstColumn);
		for (Generic attribute : attributes) {
			TableColumn<Generic, String> column = buildColumn(Objects.toString(attribute.getValue()), 200, g -> Objects.toString(g.getValue(attribute)), (g, v) -> g.updateValue(v));
			table.getColumns().add(column);
		}

		TableColumn<Generic, String> secondColumn = buildColumn("Delete", 200, g -> "Delete " + Objects.toString(g.getValue()), null);
		table.getColumns().add(secondColumn);
		table.setItems(FXCollections.observableArrayList(type.getInstances().get().collect(Collectors.toList())));

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(table);

		((Group) scene.getRoot()).getChildren().addAll(vbox);

		stage.setScene(scene);
		stage.show();
	}

	TableColumn<Generic, String> buildColumn(String columnName, int minWidth, Function<Generic, String> getter, BiConsumer<Generic, String> setter) {
		TableColumn<Generic, String> column = new TableColumn<>(columnName);
		column.setMinWidth(minWidth);
		column.setCellValueFactory(cellData -> new SimpleStringProperty(getter.apply((cellData.getValue()))));
		column.setOnEditCommit((CellEditEvent<Generic, String> t) -> {
			setter.accept((t.getTableView().getItems().get(t.getTablePosition().getRow())), t.getNewValue());
		});
		column.setCellFactory(TextFieldTableCell.<Generic> forTableColumn());
		// column.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList("1", "2", "3")));
		column.setEditable(true);
		return column;
	}
}
