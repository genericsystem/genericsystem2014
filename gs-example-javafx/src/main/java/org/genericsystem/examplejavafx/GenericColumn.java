package org.genericsystem.examplejavafx;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import org.genericsystem.mutability.Generic;

public  class GenericColumn<T> extends TableColumn<Generic,T> {
	private final Observables<T> observables = new Observables<>();
	
	@SuppressWarnings("unchecked")
	public GenericColumn(Generic attribute,String columnName,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
		super(columnName);
		setMinWidth(200);
		setCellValueFactory(cellData -> observables.get(cellData.getValue(),getter.apply(cellData.getValue())));
		setOnEditCommit((CellEditEvent<Generic, T> t) -> {
			System.out.println("coucou");
			Generic g =(t.getTableView().getItems().get(t.getTablePosition().getRow()));
			setter.accept(g, t.getNewValue());
			observables.get(g).set(t.getNewValue());
		});
		setCellFactory(tableColumn -> new EditingCell<Generic,T>(Statics.<T>getDefaultConverter((Class<T>)attribute.getClassConstraint())));
		setEditable(true);	
	}
	
	public static class Observables<T> extends HashMap<Generic,SimpleObjectProperty<T>> {

		private static final long serialVersionUID = 7709729724315030415L;

		public ObservableValue<T> get(Object key,T value) {
			SimpleObjectProperty<T> observable = super.get(key);
			if(observable==null)
				put((Generic) key,observable=new SimpleObjectProperty<T>(value));
			return observable;
		};
	};

}
