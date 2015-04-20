package org.genericsystem.examplejavafx;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.StringConverter;

import org.genericsystem.mutability.Generic;

public  class GenericColumn<T> extends TableColumn<Generic,T> {
	private final Observables<T> observables = new Observables<>();
	final Generic attribute;

	public GenericColumn(Generic attribute,String columnName, int minWidth,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
		super(columnName);
		this.attribute=attribute;
		setMinWidth(minWidth);

		setCellValueFactory(cellData -> observables.get(cellData.getValue(),getter.apply(cellData.getValue())));
		setOnEditCommit((CellEditEvent<Generic, T> t) -> {
			System.out.println("coucou");
			Generic g =(t.getTableView().getItems().get(t.getTablePosition().getRow()));
			setter.accept(g, t.getNewValue());
			observables.get(g).set(t.getNewValue());
		});
		setCellFactory(tableColumn -> getTCellFactory());
		setEditable(true);	
	}

	@SuppressWarnings("unchecked")
	TableCell<Generic, T> getTCellFactory() {
		return  new EditingCell<Generic,T>(Statics.<T>getDefaultConverter((Class<T>)attribute.getClassConstraint()),
				FXCollections.<Generic>observableArrayList(attribute.getComponents()),
				component -> new GenericStringConverter<String>(component),
				component -> FXCollections.<Generic>observableArrayList(component.getSubInstances().toList()));
	}

	private static class  Observables<T> extends HashMap<Generic,SimpleObjectProperty<T>> {

		private static final long serialVersionUID = 7709729724315030415L;

		public ObservableValue<T> get(Object key,T value) {
			SimpleObjectProperty<T> observable = super.get(key);
			if(observable==null)
				put((Generic) key,observable=new SimpleObjectProperty<T>(value));
			return observable;
		};
	};
	
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
