package org.genericsystem.javafx;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

import org.genericsystem.javafx.LinksTableView.TriConsumer;

public class LinksTableCell<G> extends TableCell<G, ObservableList<G>> {
	private final LinksTableView<G> linksTableView;

	private final G attribute;
	private final int pos;
	private final Function<G, List<G>> genericComponents;
	private final Function<G, ObservableList<G>> genericSubInstances;
	private final BiFunction<Serializable, List<G>, G> addAction;
	private final StringConverter<?> converter;

	public LinksTableCell(G attribute, StringConverter<?> converter, int pos, Function<G, ?> genericValueGetter, BiConsumer<G, ?> genericValueSetter, Function<G, List<G>> genericComponents, BiFunction<G, Integer, G> componentGetter,
			TriConsumer<G, Integer, G> componentSetter, Function<G, ObservableList<G>> genericSubInstances, BiFunction<Serializable, List<G>, G> addAction, Consumer<G> removeConsumer) {
		this.attribute = attribute;
		this.pos = pos;
		this.genericComponents = genericComponents;
		this.genericSubInstances = genericSubInstances;
		this.addAction = addAction;
		this.converter = converter;

		this.linksTableView = new LinksTableView<G>(attribute, converter, pos, genericValueGetter, genericValueSetter, genericComponents, componentGetter, componentSetter, genericSubInstances, removeConsumer);
		prefHeightProperty().bind(new SimpleIntegerProperty(112).multiply(Bindings.size(linksTableView.getItems()).add(1.01)));
		// setPrefHeight(((Generic) attribute).isSingularConstraintEnabled(0) ? 30 : 100);
		// setMinHeight(((Generic) attribute).isSingularConstraintEnabled(0) ? 30 : 100);
		// // setMaxHeight(((Generic) attribute).isSingularConstraintEnabled(0) ? 30 : 100);
		// linksTableView.setPrefHeight(((Generic) attribute).isSingularConstraintEnabled(0) ? 5 : 50);
		// linksTableView.setMinHeight(((Generic) attribute).isSingularConstraintEnabled(0) ? 5 : 50);
		// linksTableView.setMaxHeight(((Generic) attribute).isSingularConstraintEnabled(0) ? 5 : 50);
	}

	@Override
	protected void updateItem(ObservableList<G> observableLinks, boolean empty) {
		super.updateItem(observableLinks, empty);
		if (empty || observableLinks == null) {
			linksTableView.setItems(null);
			linksTableView.setContextMenu(null);
			setGraphic(null);
			setText(null);
		} else {
			G base = (G) getTableRow().getItem();
			if (base != null)
				linksTableView.setContextMenu(new AddContextMenu<>(base, attribute, converter, pos, () -> linksTableView.getItems(), getTableRow().getContextMenu(), genericComponents, genericSubInstances, addAction));
			linksTableView.setItems(observableLinks);
			setGraphic(linksTableView);
		}
	}
}