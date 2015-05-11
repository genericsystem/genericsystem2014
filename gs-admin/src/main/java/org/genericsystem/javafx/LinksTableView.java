package org.genericsystem.javafx;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.BooleanStringConverter;

import org.genericsystem.javafx.AbstractColumn.CheckBoxColumn;
import org.genericsystem.javafx.AbstractColumn.EditColumn;
import org.genericsystem.javafx.AbstractColumn.TargetComponentColumn;

public class LinksTableView<G> extends TableView<G> {

	private TableColumn<G, ?> buildColumn(String columnName, StringConverter<?> converter, Function<G, ?> genericValueGetter, BiConsumer<G, ?> genericValueSetter) {
		return BooleanStringConverter.class.equals(converter) ? new CheckBoxColumn<G>(columnName, (Function<G, Boolean>) genericValueGetter, (BiConsumer<G, Boolean>) genericValueSetter) : new EditColumn<G, Serializable>(columnName,
				(StringConverter<Serializable>) converter, (Function<G, Serializable>) genericValueGetter, (BiConsumer<G, Serializable>) genericValueSetter);
	}

	public LinksTableView(G attribute, StringConverter<?> converter, int axe, Function<G, ?> genericValueGetter, BiConsumer<G, ?> genericValueSetter, Function<G, List<G>> attributesComponents, BiFunction<G, Integer, G> linkValueGetter,
			TriConsumer<G, Integer, G> linkValueSetter, Function<G, ObservableList<G>> attributesComponentsChoice, Consumer<G> removeConsumer) {

		getColumns().add(buildColumn(attribute.toString(), converter, genericValueGetter, genericValueSetter));

		int i = 0;
		for (G component : attributesComponents.apply(attribute)) {
			if (i != axe) {
				final Integer pos = i;
				getColumns().add(new TargetComponentColumn<G>(component.toString(), (t) -> linkValueGetter.apply(t, pos), (BiConsumer<G, G>) (t, u) -> {
					linkValueSetter.accept(t, pos, u);
				}, () -> attributesComponentsChoice.apply(component)));
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
		setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		setRowFactory(new RowFactory<>(removeConsumer));
	}

	@FunctionalInterface
	public static interface TriConsumer<T, S, U> {
		void accept(T t, S s, U u);
	}
}