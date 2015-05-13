package org.genericsystem.javafx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class AddContextMenu<G> extends ContextMenu {
	public AddContextMenu(G base, G attribute, StringConverter<?> converter, int axe, Supplier<ObservableList<G>> items, ContextMenu aboveMenu, Function<G, List<G>> typeComponents, Function<G, ObservableList<G>> typeComponentSubInstances,
			BiFunction<Serializable, List<G>, G> addAction) {
		String text = "Add New " + attribute.toString() + (base != null ? " on : " + base.toString() : "");
		final MenuItem addItem = new MenuItem(text, new ImageView(new Image(getClass().getResourceAsStream("ok.png"))));
		addItem.setOnAction((EventHandler<ActionEvent>) e -> {
			new AddDialog<>(base, attribute, converter, axe, items.get(), text, typeComponents, typeComponentSubInstances, addAction).showAndWait();
		});
		getItems().add(addItem);
		if (aboveMenu != null) {
			aboveMenu.getItems().forEach(item -> {
				MenuItem newItem = new MenuItem(item.getText(), new ImageView(((ImageView) item.getGraphic()).getImage()));
				getItems().add(newItem);
				newItem.onActionProperty().bind(item.onActionProperty());
			});
		}
	}

	@FunctionalInterface
	public interface TriFunction<T, U, V, R> {
		R apply(T t, U u, V v);
	}

	public static class AddDialog<G> extends Dialog<G> {
		public AddDialog(G instance, G type, StringConverter<?> converter, int axe, ObservableList<G> tableItems, String headerText, Function<G, List<G>> typeComponents, Function<G, ObservableList<G>> typeComponentSubInstances,
				BiFunction<Serializable, List<G>, G> addAction) {

			setTitle("Add an instance");
			setHeaderText(headerText);
			setResizable(true);

			GridPane grid = new GridPane();
			Label label1 = new Label("Value : ");
			TextField text1 = new TextField();
			grid.add(label1, 0, 0);
			grid.add(text1, 1, 0);
			int i = 0;
			List<ComboBox<G>> combos = new ArrayList<>();
			for (G typeComponent : typeComponents.apply(type)) {
				Label label = new Label("" + typeComponent);
				ComboBox<G> combo = new ComboBox<>();
				combo.setItems(typeComponentSubInstances.apply(typeComponent));
				combos.add(combo);
				if (i == axe) {
					combo.getSelectionModel().select(instance);
					combo.setDisable(true);
				}
				grid.add(label, 0, i + 1);
				grid.add(combo, 1, i + 1);
				i++;
			}
			getDialogPane().setContent(grid);
			getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			setResultConverter(buttonType -> {
				if (buttonType == ButtonType.OK) {
					List<G> components = combos.stream().map(combo -> combo.getSelectionModel().getSelectedItem()).collect(Collectors.toList());
					G generic = addAction.apply((Serializable) converter.fromString(text1.getText()), components);
					tableItems.add(generic);
					return generic;
				}
				return null;
			});
		}
	}

}