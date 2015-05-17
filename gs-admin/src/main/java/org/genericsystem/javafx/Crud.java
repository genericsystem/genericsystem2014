package org.genericsystem.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

import org.genericsystem.admin.UiFunctions;

/**
 * @author Nicolas Feybesse
 *
 */
public class Crud<G> extends VBox {

	public Crud(G metaType, UiFunctions<G> gsFunctions) {

		// setSpacing(5);
		setPadding(new Insets(10, 10, 10, 10));
		ObjectProperty<G> typeProperty = new SimpleObjectProperty<G>(metaType);
		ObjectProperty<G> typeProperty2 = new SimpleObjectProperty<G>(null);
		InstancesTableView<G> table = new InstancesTableView<>(typeProperty, gsFunctions);
		InstancesTableView<G> table2 = new InstancesTableView<>(typeProperty2, gsFunctions);

		getChildren().addAll(table, table2);
		typeProperty2.bind(table.getSelectionModel().selectedItemProperty());
	}
}
