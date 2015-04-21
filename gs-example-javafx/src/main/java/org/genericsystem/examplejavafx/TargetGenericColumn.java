package org.genericsystem.examplejavafx;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;

import org.genericsystem.examplejavafx.GenericColumn.Observables;
import org.genericsystem.mutability.Generic;

public class TargetGenericColumn extends TableColumn<Generic,Generic> {
	private final Observables<Generic> observables = new Observables<>();

	public TargetGenericColumn(Generic targetComponent,String columnName,Function<Generic, Generic> getter,BiConsumer<Generic, Generic> setter) {
		super(columnName);
		setMinWidth(200);
		setCellValueFactory(cellData -> observables.get(cellData.getValue(),getter.apply(cellData.getValue())));
		setOnEditCommit((CellEditEvent<Generic, Generic> event) -> {
			System.out.println("coucou");
			Generic generic =(event.getTableView().getItems().get(event.getTablePosition().getRow()));
			setter.accept(generic, event.getNewValue());
			observables.get(generic).set(event.getNewValue());
		});
		setCellFactory(tableColumn -> new ComboBoxTableCell<Generic,Generic>(new GenericStringConverter<>(targetComponent),FXCollections.<Generic>observableArrayList(targetComponent.getSubInstances().toList())));
		setEditable(true);	
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
