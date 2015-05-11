package org.genericsystem.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.javafx.AbstractColumn;
import org.genericsystem.javafx.AddContextMenu.TriFunction;
import org.genericsystem.javafx.InstancesTableView;
import org.genericsystem.javafx.LinksTableView.TriConsumer;
import org.genericsystem.mutability.Generic;

/**
 * @author Nicolas Feybesse
 *
 */
public class Crud extends VBox {

	private static final Consumer<Generic> removeConsumer = generic -> {
		generic.remove();
		System.out.println("Remove from GS : " + generic.info());
	};

	private static final Function<Generic, ?> genericValueGetter = generic -> generic.getValue();
	private static final BiConsumer<Generic, ?> genericValueSetter = (generic, value) -> {
		generic.updateValue((Serializable) value);
	};
	private static final Function<Generic, List<Generic>> genericComponents = generic -> generic.getComponents();

	private static final BiFunction<Generic, Integer, Generic> genericComponentGetter = (generic, pos) -> generic.getComponent(pos);

	private static final TriConsumer<Generic, Integer, Generic> genericComponentSetter = (generic, pos, target) -> {
		List<Generic> components = new ArrayList<>(generic.getComponents());
		components.set(pos, target);
		generic.update(generic.getValue(), components.toArray(new Generic[components.size()]));
		System.out.println("Update in GS : " + generic.info());
	};

	private static final TriFunction<Generic, Serializable, List<Generic>, Generic> attributeAddAction = (typ, value, components) -> {
		Generic generic = typ.addInstance(value, components.toArray(new Generic[components.size()]));
		System.out.println("Add into GS : " + generic);
		return generic;
	};

	private static final Function<Generic, StringConverter<?>> attributeConverter = attribute -> AbstractColumn.getDefaultInstanceValueStringConverter(attribute.getValueInstanceClassConstraint());

	private static final Function<Generic, Function<Generic, ObservableList<Generic>>> attributeGetter = attribute -> generic -> FXCollections.observableArrayList((generic.getHolders(attribute).toList()));

	private static final Function<Generic, Snapshot<Generic>> typeAttributes = type -> type.getAttributes().filter(attribute -> type.inheritsFrom(attribute.getComponent(ApiStatics.BASE_POSITION)));

	private static final Function<Generic, ObservableList<Generic>> genericSubInstances = targetComponent -> FXCollections.observableArrayList((targetComponent.getSubInstances().toList()));

	public Crud(Generic type) {
		// setSpacing(5);
		setPadding(new Insets(10, 10, 10, 10));
		ObjectProperty<Generic> typeProperty = new SimpleObjectProperty<Generic>(type.getMeta());
		ObjectProperty<Generic> typeProperty2 = new SimpleObjectProperty<Generic>(type);
		InstancesTableView<Generic> table = new InstancesTableView<>(typeProperty, genericSubInstances, typeAttributes, genericComponents, genericValueGetter, genericValueSetter, genericComponentGetter, genericComponentSetter, attributeAddAction,
				attributeConverter, attributeGetter, removeConsumer);
		InstancesTableView<Generic> table2 = new InstancesTableView<>(typeProperty2, genericSubInstances, typeAttributes, genericComponents, genericValueGetter, genericValueSetter, genericComponentGetter, genericComponentSetter, attributeAddAction,
				attributeConverter, attributeGetter, removeConsumer);

		getChildren().addAll(table, table2);
		typeProperty2.bind(table.getSelectionModel().selectedItemProperty());
	}
}
