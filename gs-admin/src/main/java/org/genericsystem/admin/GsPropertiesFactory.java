package org.genericsystem.admin;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import org.genericsystem.mutability.Generic;

public class GsPropertiesFactory<T> extends HashMap<Generic,Property<T>>  implements Callback<CellDataFeatures<Generic,T>, ObservableValue<T>> {

	private static final long serialVersionUID = 7709729724315030415L;
	private final  Function<Generic, T> getter;
	private final BiConsumer<Generic, T> setter;
	
	public GsPropertiesFactory(Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
		this.getter = getter;
		this.setter = setter;
	}
	
	public ObservableValue<T> get(Object key,T value) {
		Property<T> observable = super.get(key);
		if(observable==null) {
			put((Generic) key,observable=buildProperty(value));
			observable.addListener(new ChangeListener<T>() {
				@Override
				public void changed(ObservableValue<? extends T> observable,T oldValue, T newValue) {
					System.out.println("Change : "+oldValue+" "+newValue);
					setter.accept((Generic) key, newValue);
				}
			});
		}
		return observable;
	};

	Property<T> buildProperty(T value){
		return new SimpleObjectProperty<T>(value);
	}

	@Override
	public ObservableValue<T> call(CellDataFeatures<Generic, T> cellData) {
		return get(cellData.getValue(),getter.apply(cellData.getValue()));
	}
}