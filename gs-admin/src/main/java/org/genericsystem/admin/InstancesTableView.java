package org.genericsystem.admin;


import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import org.genericsystem.admin.AbstractColumn.EditColumn;
import org.genericsystem.admin.AbstractColumn.GenericComponentColumn;
import org.genericsystem.admin.AbstractColumn.GenericStringConverter;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.mutability.Generic;


/**
 * @author Nicolas Feybesse
 *
 */
public class InstancesTableView extends TableView<Generic>{


	public static class AddDialog extends Dialog<Generic> {
		public AddDialog(Generic type,ObservableList<Generic> tableItems,String title,String headerText) {
			setTitle(title);
			setHeaderText(headerText);
			setResizable(true);

			Label label1 = new Label("Value : ");
			Label label2 = new Label("Other : ");
			TextField text1 = new TextField();
			TextField text2 = new TextField();

			GridPane grid = new GridPane();
			grid.add(label1, 1, 1);
			grid.add(text1, 2, 1);
			grid.add(label2, 1, 2);
			grid.add(text2, 2, 2);
			getDialogPane().setContent(grid);
			getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
			setResultConverter(buttonType->{
				if (buttonType == ButtonType.OK) {
					Generic generic = type.setInstance(text1.getText());
					if(!tableItems.contains(generic))
						tableItems.add(generic);
					return generic;
				}
				return null;
			});
		}
	}

	public InstancesTableView(Generic type) {
		//setPrefWidth(1800);
		//setPrefWidth(1580);
		final ContextMenu menu = new ContextMenu();
		String text = "Add New : "+new GenericStringConverter<>(type.getMeta()).toString(type);
		final MenuItem addItem = new MenuItem(text,new ImageView(new Image(getClass().getResourceAsStream("ok.png"))));
		addItem.setOnAction( (EventHandler<ActionEvent>) e->  {
			new AddDialog(type,getItems(),"Add an instance",text).showAndWait().ifPresent(res->System.out.println("Result: " + res));
		});
		menu.getItems().add(addItem);
		setContextMenu(menu);

		TableColumn<Generic, ?> firstColumn = new EditColumn<>(type, g -> g.getValue(), (g, v) -> {
			if(type.getInstance(v)!=null)
				throw new IllegalStateException();
			g.updateValue(v);
		});
		getColumns().add(firstColumn);


		for (Generic attribute : type.getAttributes().filter(attribute->type.inheritsFrom(attribute.getComponent(ApiStatics.BASE_POSITION)))) {
			getColumns().add(new GenericComponentColumn(attribute,attribute.getComponents().indexOf(type),g -> g.getHolders(attribute), null));
		}
		//getColumns().add(new DeleteColumn(type));
		setEditable(true);



		ObservableList<Generic> data = FXCollections.observableArrayList(type.getSubInstances().stream().collect(Collectors.toList()));
		data.addListener((ListChangeListener<Generic>)e->{
			while (e.next()) {
				e.getRemoved().forEach(g-> {
					g.remove();
					System.out.println("Remove from GS : "+g.info());
				});
			}
		});

		//		setFixedCellSize(100);
		//	    prefHeightProperty().bind(fixedCellSizeProperty().multiply(Bindings.size(getItems()).add(1.01)));
		//	    minHeightProperty().bind(prefHeightProperty());
		//	    maxHeightProperty().bind(prefHeightProperty());
		setRowFactory(new Callback<TableView<Generic>, TableRow<Generic>>() {
			@Override
			public TableRow<Generic> call(TableView<Generic> tableView) {
				final TableRow<Generic> row = new TableRow<>();
				final ContextMenu rowMenu = new ContextMenu();
				final ContextMenu tableMenu = tableView.getContextMenu();
				if (tableMenu != null) {
					tableMenu.getItems().forEach(item->{
						MenuItem newItem = new MenuItem(item.getText(),item.getGraphic());
						newItem.onActionProperty().bind(item.onActionProperty());
						rowMenu.getItems().add(newItem);
					});
					//rowMenu.getItems().add(new SeparatorMenuItem());
				}


				final MenuItem deleteItem = new MenuItem("Delete : "+ new GenericStringConverter<>(type).toString(row.getItem()),new ImageView(new Image(getClass().getResourceAsStream("not.png"))));
				deleteItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Confirmation Dialog");
						alert.setHeaderText("Confirmation is required");
						alert.setContentText("Delete : "+ new GenericStringConverter<>(type).toString(getSelectionModel().getSelectedItem()) +" ?");
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK)
							getItems().remove(getSelectionModel().getSelectedItem());
					}
				});
				// disable this menu item if nothing is selected:
				deleteItem.disableProperty().bind(
						Bindings.isEmpty(getSelectionModel().getSelectedItems()));
				deleteItem.textProperty().bind(Bindings.createStringBinding(()->"Delete : "+new GenericStringConverter<>(type).toString(row.getItem()),row.itemProperty()));
				rowMenu.getItems().add(deleteItem);

				// only display context menu for non-null items:
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty()))
						.then(rowMenu)
						.otherwise((ContextMenu)null));
				return row;
			}
		});
		setItems(data);
	}
}

