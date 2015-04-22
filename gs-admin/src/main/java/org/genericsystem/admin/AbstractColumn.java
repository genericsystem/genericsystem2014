package org.genericsystem.admin;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

import org.genericsystem.api.core.AxedPropertyClass;
import org.genericsystem.mutability.Generic;

/**
 * @author Nicolas Feybesse
 *
 * @param <T>
 */
public class AbstractColumn<T> extends TableColumn<Generic,T> {
	protected final Observables<T> observables = new Observables<>();

	public AbstractColumn(Generic generic,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
		super(getDefaultConverter(generic.getMeta()).toString(generic.getValue()));
		setMinWidth(200);
		setCellValueFactory(cellData -> observables.get(cellData.getValue(),getter.apply(cellData.getValue())));
		setOnEditCommit((CellEditEvent<Generic, T> t) -> {
			System.out.println("coucou");
			Generic g =(t.getTableView().getItems().get(t.getTablePosition().getRow()));
			setter.accept(g, t.getNewValue());
			observables.get(g).set(t.getNewValue());
		});
		setEditable(true);	
	}

	public static class GenericColumn<T> extends AbstractColumn<T>{
		@SuppressWarnings("unchecked")
		public GenericColumn(Generic attribute,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
			super(attribute, getter, setter);
			setCellFactory(tableColumn -> new EditingCell<Generic,T>(getDefaultConverter((Class<T>)attribute.getClassConstraint())));	
		}
	}

	public static class TargetComponentColumn extends AbstractColumn<Generic>{
		public TargetComponentColumn(Generic targetComponent,Function<Generic, Generic> getter,BiConsumer<Generic, Generic> setter) {
			super(targetComponent, getter, setter);
			setCellFactory(tableColumn -> new ComboBoxTableCell<Generic,Generic>(new GenericStringConverter<>(targetComponent),FXCollections.<Generic>observableArrayList(targetComponent.getSubInstances().toList())));	
		}
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

	static class GenericStringConverter<T extends Serializable> extends StringConverter<Generic> {

		private final StringConverter<T> instanceValueClassConverter;

		GenericStringConverter(Generic component){
			this.instanceValueClassConverter = getDefaultConverter(component);
		}

		@SuppressWarnings("unchecked")
		@Override
		public String toString(Generic generic) {
			return instanceValueClassConverter.toString((T)generic.getValue());
		}

		@Override
		public Generic fromString(String string) {
			throw new IllegalStateException();
		}	
	}
	
	@SuppressWarnings("unchecked")
	static <T> StringConverter<T> getDefaultConverter(Generic generic){
		return getDefaultConverter((Class<T>) generic.getClassConstraint());
	}

	@SuppressWarnings("unchecked")
	static <T> StringConverter<T> getDefaultConverter(Class<T> clazz){
//		if(Boolean.class.equals(clazz)) {
//			return (StringConverter<T>) new BooleanStringConverter();
//		}
		if(Integer.class.equals(clazz))
			return (StringConverter<T>) new IntegerStringConverter();
		if(String.class.equals(clazz))
			return (StringConverter<T>) new DefaultStringConverter();
		return new StringConverter<T>(){

			@Override
			public String toString(Object object) {
				if(object==null)
					return "null";
				if(object instanceof Class)
					return Objects.toString(((Class<?>)object).getSimpleName());
				if(object instanceof AxedPropertyClass)
					return Objects.toString(((AxedPropertyClass)object).getClazz().getSimpleName()+((AxedPropertyClass)object).getAxe());
				return Objects.toString(object);
			}

			@Override
			public T fromString(String string) {
				throw new IllegalStateException();
			}

		};
	}

}