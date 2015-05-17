package org.genericsystem.javafx;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.BooleanStringConverter;

import org.genericsystem.admin.UiFunctions.AttributeUiFunctions;
import org.genericsystem.javafx.AbstractColumn.CheckBoxColumn;
import org.genericsystem.javafx.AbstractColumn.EditColumn;
import org.genericsystem.javafx.AbstractColumn.TargetComponentColumn;
import org.genericsystem.mutability.Generic;

public class LinksTableView<G> extends TableView<G> {

	public static <G> TableColumn<G, ?> buildColumn(String columnName, StringConverter<Serializable> converter, Function<G, Serializable> genericValueGetter, BiConsumer<G, Serializable> genericValueSetter) {
		return BooleanStringConverter.class.equals(converter) ? new CheckBoxColumn<G>(columnName, (Function) genericValueGetter, (BiConsumer) genericValueSetter) : new EditColumn<G, Serializable>(columnName, converter, genericValueGetter,
				genericValueSetter);
	}

	public LinksTableView(G attribute, AttributeUiFunctions<G> attFunctions, int axe) {
		if (((Generic) attribute).getInstanceValueGenerator() == null)
			getColumns().add(buildColumn(attribute.toString(), attFunctions.converter, attFunctions.genericGetter, attFunctions.genericSetter));

		int i = 0;
		for (G component : attFunctions.genericComponents.apply(attribute)) {
			if (i != axe) {
				final Integer pos = i;
				getColumns().add(new TargetComponentColumn<G>(component.toString(), (t) -> attFunctions.genericComponentGetter.apply(t, pos), (BiConsumer<G, G>) (t, u) -> {
					attFunctions.genericComponentSetter.accept(t, pos, u);
				}, () -> attFunctions.genericSubInstances.apply(component)));
			}
			i++;
		}

		setEditable(true);
		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				// Get the table header
				Pane header = (Pane) lookup("TableHeaderRow");
				if (header != null && header.isVisible()) {
					header.setMaxHeight(4);
					header.setMinHeight(4);
					header.setPrefHeight(4);
					header.setVisible(true);
					header.setManaged(true);
				}
			}
		});
		setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		setRowFactory(new RowFactory<>(attFunctions.removeConsumer));
	}

	@FunctionalInterface
	public static interface TriConsumer<T, S, U> {
		void accept(T t, S s, U u);
	}
}