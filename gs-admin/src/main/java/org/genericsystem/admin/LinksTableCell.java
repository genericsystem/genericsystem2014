package org.genericsystem.admin;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.genericsystem.admin.AbstractColumn.GenericStringConverter;
import org.genericsystem.admin.App.LinksObservableList;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Generic;

public class LinksTableCell extends TableCell<Generic, Snapshot<Generic>> {
	private final LinksTableView linksTableView;
	private final Generic attribute;

	public LinksTableCell(Generic attribute, int pos) {
		this.attribute = attribute;
		this.linksTableView = new LinksTableView(attribute, pos);
		prefHeightProperty().bind(new SimpleIntegerProperty(112).multiply(Bindings.size(linksTableView.getItems()).add(1.01)));
		minHeightProperty().bind(prefHeightProperty());
		maxHeightProperty().bind(prefHeightProperty());
	}

	@Override
	protected void updateItem(Snapshot<Generic> links, boolean empty) {
		super.updateItem(links, empty);
		if (empty || links == null) {
			linksTableView.setItems(null);
			setGraphic(null);
			setText(null);
		} else {
			if (getTableRow().getItem() != null) {
				final ContextMenu menu = new ContextMenu();
				final ContextMenu tableMenu = getTableRow().getContextMenu();
				String text = "Add New " + new GenericStringConverter<>(attribute.getMeta()).toString(attribute) + " on : " + new GenericStringConverter<>(((Generic) getTableRow().getItem()).getMeta()).toString((Generic) getTableRow().getItem());
				final MenuItem addItem = new MenuItem(text, new ImageView(new Image(getClass().getResourceAsStream("ok.png"))));
				addItem.setOnAction((EventHandler<ActionEvent>) e -> {
					new AddDialog((Generic) getTableRow().getItem(), attribute, linksTableView.getItems(), "Add an instance", text).showAndWait().ifPresent(res -> System.out.println("Result: " + res));
				});
				menu.getItems().add(addItem);

				if (tableMenu != null) {
					tableMenu.getItems().forEach(item -> {
						MenuItem newItem = new MenuItem(item.getText(), item.getGraphic());
						menu.getItems().add(newItem);
						newItem.onActionProperty().bind(item.onActionProperty());
					});
				}

				linksTableView.setContextMenu(menu);
			}
			linksTableView.setItems(new LinksObservableList(links));
			setGraphic(linksTableView);
		}
	};
}