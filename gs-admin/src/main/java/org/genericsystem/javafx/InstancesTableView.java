package org.genericsystem.javafx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.genericsystem.javafx.AbstractColumn.TargetComponentColumn;
import org.genericsystem.javafx.LinksTableView.TriConsumer;
import org.genericsystem.mutability.Generic;

//import org.genericsystem.mutability.Generic;

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
	private final Function<G, BiFunction<Serializable, List<G>, G>> attributeAddAction;
	private final Function<G, List<G>> genericComponents;
	private final Consumer<G> removeConsumer;
	private final BiFunction<G, Integer, G> genericComponentGetter;
	private final TriConsumer<G, Integer, G> genericComponentSetter;
	private final Function<G, ObservableList<G>> genericSubInstances;
	private final ObjectBinding<ContextMenu> contextMenuBinding;
	// private final ObjectBinding<Callback<TableView<G>, TableRow<G>>> rowFactory;
	private final ListBinding<TableColumn<G, ?>> columnsBinding;
	private final ListBinding<G> itemsBinding;

	public InstancesTableView(ObservableValue<G> observableType, Function<G, ObservableList<G>> genericSubInstances, Function<G, Snapshot<G>> typeAttributes, Function<G, List<G>> genericComponents, Function<G, ?> genericGetter,
			BiConsumer<G, ?> genericSetter, BiFunction<G, Integer, G> genericComponentGetter, TriConsumer<G, Integer, G> genericComponentSetter, Function<G, BiFunction<Serializable, List<G>, G>> attributeAddAction,
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

		setEditable(true);
		contextMenuProperty().bind(contextMenuBinding = new ObjectBinding<ContextMenu>() {
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
				return observableType.getValue() != null ? new AddContextMenu<G>(null, observableType.getValue(), attributeConverter.apply(observableType.getValue()), -1, () -> getItems(), null, genericComponents, genericSubInstances, attributeAddAction
						.apply(observableType.getValue())) : null;
			}
		});

		// rowFactoryProperty().bind(rowFactory = new ObjectBinding<Callback<TableView<G>, TableRow<G>>>() {
		// {
		// super.bind(observableType);
		// }
		//
		// @Override
		// public void dispose() {
		// super.unbind(observableType);
		// }
		//
		// @Override
		// protected Callback<TableView<G>, TableRow<G>> computeValue() {
		// System.out.println("Compute row factory for : " + observableType.getValue());
		// return new RowFactory<>(removeConsumer);
		// }
		// });
		setRowFactory(new RowFactory<>(removeConsumer));
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
				System.out.println("Compute columns menu for : " + observableType.getValue());
				return FXCollections.observableArrayList(buildColumns(observableType));
			}
		});
		itemsProperty().set(itemsBinding = new ListBinding<G>() {
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
				if ((((Generic) attribute).isSingularConstraintEnabled(pos) || ((Generic) attribute).isPropertyConstraintEnabled()) && genericComponents.apply(attribute).size() == 1)
					columns.add(LinksTableView.<G> buildColumn(attribute.toString(), attributeConverter.apply(attribute), (g) -> genericGetter.apply(attributeGetter.apply(attribute).apply(g).stream().findFirst().orElse(null)), (base, newValue) -> {
						G link = attributeGetter.apply(attribute).apply(base).stream().findFirst().orElse(null);
						if (link != null)
							((BiConsumer<G, Serializable>) genericSetter).accept(link, (Serializable) newValue);
						else
							attributeAddAction.apply(attribute).apply((Serializable) newValue, Arrays.asList(base));
					}));
				else if (((Generic) attribute).isSingularConstraintEnabled(pos) && genericComponents.apply(attribute).size() == 2 && ((Generic) attribute).getInstanceValueGenerator() != null)
					columns.add(new TargetComponentColumn<G>(attribute.toString(), (g) -> genericComponentGetter.apply(attributeGetter.apply(attribute).apply(g).stream().findFirst().orElse(null), pos == 0 ? 1 : 0), (base, newTarget) -> {
						G link = attributeGetter.apply(attribute).apply(base).stream().findFirst().orElse(null);
						if (link != null)
							genericComponentSetter.accept(link, pos == 0 ? 1 : 0, newTarget);
						else {
							List<G> components = new ArrayList<G>(genericComponents.apply(base));
							components.set(pos == 0 ? 1 : 0, newTarget);
							attributeAddAction.apply(attribute).apply(null, components);
						}
					}, () -> genericSubInstances.apply(genericComponentGetter.apply(attribute, pos == 0 ? 1 : 0))));
				else
					columns.add(new GenericComponentColumn<G>(attribute, attributeConverter.apply(attribute), pos, attributeGetter.apply(attribute), genericGetter, genericSetter, genericComponents, genericComponentGetter, genericComponentSetter,
							genericSubInstances, attributeAddAction.apply(attribute), removeConsumer));
			}
		}
		return columns;
	}
}
