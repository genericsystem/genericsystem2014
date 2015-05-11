package org.genericsystem.javafx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.javafx.AbstractColumn.EditColumn;
import org.genericsystem.javafx.AbstractColumn.GenericComponentColumn;
import org.genericsystem.javafx.AddContextMenu.TriFunction;
import org.genericsystem.javafx.LinksTableView.TriConsumer;

/**
 * @author Nicolas Feybesse
 *
 */
public class InstancesTableView<G> extends TableView<G> {

	private final Function<G, Snapshot<G>> typeAttributes;
	private final Function<G, StringConverter<?>> attributeConverter;
	private final Function<G, ?> genericGetter;
	private final Function<G, Function<G, ObservableList<G>>> attributeGetter;
	private final BiConsumer<G, ?> genericSetter;
	private final TriFunction<G, Serializable, List<G>, G> attributeAddAction;
	private final Function<G, List<G>> genericComponents;
	private final Consumer<G> removeConsumer;
	private final BiFunction<G, Integer, G> genericComponentGetter;
	private final TriConsumer<G, Integer, G> genericComponentSetter;
	private final Function<G, ObservableList<G>> genericSubInstances;
	private final ListBinding<TableColumn<G, ?>> columnsBinding;

	public InstancesTableView(ObservableValue<G> observableType, Function<G, ObservableList<G>> genericSubInstances, Function<G, Snapshot<G>> typeAttributes, Function<G, List<G>> genericComponents, Function<G, ?> genericGetter,
			BiConsumer<G, ?> genericSetter, BiFunction<G, Integer, G> genericComponentGetter, TriConsumer<G, Integer, G> genericComponentSetter, TriFunction<G, Serializable, List<G>, G> attributeAddAction,
			Function<G, StringConverter<?>> attributeConverter, Function<G, Function<G, ObservableList<G>>> attributeGetter, Consumer<G> removeConsumer) {
		this.typeAttributes = typeAttributes;
		this.attributeConverter = attributeConverter;
		this.genericGetter = genericGetter;
		this.genericSetter = genericSetter;
		this.attributeGetter = attributeGetter;
		this.attributeAddAction = attributeAddAction;
		this.genericComponents = genericComponents;

		this.removeConsumer = removeConsumer;

		this.genericComponentGetter = genericComponentGetter;
		this.genericComponentSetter = genericComponentSetter;

		this.genericSubInstances = genericSubInstances;

		// getColumns().setAll(initColumns());
		setEditable(true);
		contextMenuProperty().bind(new ObjectBinding<ContextMenu>() {
			{
				super.bind(observableType);
			}

			@Override
			public void dispose() {
				super.unbind(observableType);
			}

			@Override
			protected ContextMenu computeValue() {
				System.out.println("Compute context menu for : " + observableType.getValue());
				return observableType.getValue() != null ? new AddContextMenu<G>(null, observableType.getValue(), attributeConverter.apply(observableType.getValue()), -1, () -> getItems(), null, genericComponents, genericSubInstances, attributeAddAction)
						: null;
			}
		});

		Bindings.bindContent(getColumns(), columnsBinding = new ListBinding<TableColumn<G, ?>>() {
			{
				super.bind(observableType);
			}

			@Override
			public void dispose() {
				super.unbind(observableType);
			}

			@Override
			protected ObservableList<TableColumn<G, ?>> computeValue() {
				return FXCollections.observableArrayList(buildColumns(observableType));
			}
		});

		setRowFactory(new RowFactory<>(removeConsumer));
		itemsProperty().set(new ListBinding<G>() {
			{
				super.bind(observableType);
			}

			@Override
			public void dispose() {
				super.unbind(observableType);
			}

			@Override
			protected ObservableList<G> computeValue() {
				System.out.println("Compute items for : " + observableType.getValue());
				return observableType.getValue() != null ? FXCollections.observableArrayList(genericSubInstances.apply(observableType.getValue())) : FXCollections.emptyObservableList();
			}
		});
	}

	private List<TableColumn<G, ?>> buildColumns(ObservableValue<G> observableType) {
		List<TableColumn<G, ?>> columns = new ArrayList<TableColumn<G, ?>>();
		if (observableType.getValue() != null) {
			columns.add(new EditColumn<G, Serializable>(observableType.getValue().toString(), (StringConverter<Serializable>) attributeConverter.apply(observableType.getValue()), (Function<G, Serializable>) genericGetter,
					(BiConsumer<G, Serializable>) genericSetter));
			for (G attribute : typeAttributes.apply(observableType.getValue())) {
				int pos = genericComponents.apply(attribute).indexOf(observableType.getValue());
				columns.add(new GenericComponentColumn<G>(attribute, attributeConverter.apply(attribute), pos, attributeGetter.apply(attribute), genericGetter, genericSetter, genericComponents, genericComponentGetter, genericComponentSetter,
						genericSubInstances, attributeAddAction, removeConsumer));

			}
		}
		return columns;
	}
}
