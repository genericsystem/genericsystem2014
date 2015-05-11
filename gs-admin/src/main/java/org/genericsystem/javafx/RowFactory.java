package org.genericsystem.javafx;

import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class RowFactory<G> implements Callback<TableView<G>, TableRow<G>> {

	private final Consumer<G> removeConsumer;

	public RowFactory(Consumer<G> removeConsumer) {
		this.removeConsumer = removeConsumer;
	}

	@Override
	public TableRow<G> call(TableView<G> tableView) {
		final TableRow<G> row = new TableRow<>();
		final ContextMenu rowMenu = new DeleteContextMenu<G>(row, () -> tableView.getItems(), tableView.getContextMenu(), removeConsumer);
		row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu).otherwise((ContextMenu) null));
		return row;
	}

}