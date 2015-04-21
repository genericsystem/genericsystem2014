package org.genericsystem.examplejavafx;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;

import org.genericsystem.examplejavafx.GenericColumn.Observables;
import org.genericsystem.mutability.Generic;

public class TargetGenericColumn extends TableColumn<Generic,Generic> {
	private final Observables<Generic> observables = new Observables<>();
	final Generic attribute;

	public TargetGenericColumn(Generic attribute,String columnName, int minWidth,Function<Generic, Generic> getter,BiConsumer<Generic, Generic> setter) {
		super(columnName);
		this.attribute=attribute;
		setMinWidth(minWidth);

		setCellValueFactory(cellData -> observables.get(cellData.getValue(),getter.apply(cellData.getValue())));
		setOnEditCommit((CellEditEvent<Generic, Generic> t) -> {
			System.out.println("coucou");
			Generic g =(t.getTableView().getItems().get(t.getTablePosition().getRow()));
			setter.accept(g, t.getNewValue());
			observables.get(g).set(t.getNewValue());
		});
		setCellFactory(tableColumn -> getTCellFactory());
		setEditable(true);	
	}

	TableCell<Generic, Generic> getTCellFactory() {
		return new ComboBoxTableCell<Generic,Generic>(new GenericStringConverter<>(attribute.getTargetComponent()),FXCollections.<Generic>observableArrayList(attribute.getTargetComponent().getSubInstances().toList()));
	}
	
	static class GenericStringConverter<T extends Serializable> extends StringConverter<Generic> {

		private final StringConverter<T> instanceValueClassConverter;

		@SuppressWarnings("unchecked")
		GenericStringConverter(Generic component){
			this.instanceValueClassConverter = Statics.getDefaultConverter((Class<T>)component.getClassConstraint());
		}

		@SuppressWarnings("unchecked")
		@Override
		public String toString(Generic generic) {
			return instanceValueClassConverter.toString((T)generic.getValue());
		}

		@Override
		public Generic fromString(String string) {
			throw new IllegalStateException();
			//return component.getInstance(instanceValueClassConverter.fromString(string));
		}	
	}
}
