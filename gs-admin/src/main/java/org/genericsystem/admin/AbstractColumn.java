package org.genericsystem.admin;


import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
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
public abstract class AbstractColumn<T> extends TableColumn<Generic,T> {
	
	public AbstractColumn(Generic generic,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
		super(getDefaultConverter(generic.getMeta()).toString(generic.getValue()));
		GsPropertiesFactory<T> observables = buildObservables(getter,setter);
		setMinWidth(200);
		setCellValueFactory(observables);
		setOnEditCommit((CellEditEvent<Generic, T> t) -> {
			Generic g =(t.getTableView().getItems().get(t.getTablePosition().getRow()));
			observables.get(g).setValue(t.getNewValue());
		});
		setEditable(true);	
	}

	protected GsPropertiesFactory<T> buildObservables(Function<Generic, T> getter,BiConsumer<Generic, T> setter){
		return new GsPropertiesFactory<>(getter,setter);
	}


	public static class CheckBoxColumn extends AbstractColumn<Boolean>{
		public CheckBoxColumn(Generic attribute,Function<Generic, Boolean> getter,BiConsumer<Generic, Boolean> setter) {
			super(attribute, getter, setter);
			setCellFactory(CheckBoxTableCell.<Generic>forTableColumn(this));
		}

		@Override
		public GsPropertiesFactory<Boolean> buildObservables(Function<Generic, Boolean> getter,BiConsumer<Generic, Boolean> setter) {
			return new GsPropertiesFactory<Boolean>(getter,setter){
				private static final long serialVersionUID = 2551229764188937954L;

				@Override
				Property<Boolean> buildProperty(Boolean value){
					return new SimpleBooleanProperty(Boolean.TRUE.equals(value));
				}
			};
		}
	}
	
	


	public static class EditColumn<T> extends AbstractColumn<T>{
		public EditColumn(Generic attribute,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
			super(attribute, getter, setter);
			setCellFactory(tableColumn -> new EditingTableCell<Generic,T>(getDefaultConverter(attribute)));	
		}
	}


	public static class TargetComponentColumn extends AbstractColumn<Generic>{
		public TargetComponentColumn(Generic targetComponent,Function<Generic, Generic> getter,BiConsumer<Generic, Generic> setter) {
			super(targetComponent, getter, setter);
			setCellFactory(ChoiceBoxTableCell.<Generic,Generic>forTableColumn(new GenericStringConverter<>(targetComponent),FXCollections.<Generic>observableArrayList(targetComponent.getSubInstances().toList())));
		}
	}
	
	public static class DeleteColumn extends TableColumn<Generic, Generic> {
		public DeleteColumn(Generic type) {
			super("Delete");
			setMinWidth(200);
			setEditable(true);
			setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
			setCellFactory(column->new DeleteButtonTableCell<>(new GenericStringConverter<>(type)));
		}
	}

	static class GenericStringConverter<T extends Serializable> extends StringConverter<Generic> {

		private final StringConverter<T> instanceValueClassConverter;

		GenericStringConverter(Generic component){
			this.instanceValueClassConverter = getDefaultConverter(component);
		}

		@SuppressWarnings("unchecked")
		@Override
		public String toString(Generic generic) {
			return generic!=null ? instanceValueClassConverter.toString((T)generic.getValue()):null;
		}

		@Override
		public Generic fromString(String string) {
			throw new IllegalStateException();
		}	
	}

	@SuppressWarnings("unchecked")
	static <T> StringConverter<T> getDefaultConverter(Generic generic){
		Class<T> clazz = (Class<T>) generic.getClassConstraint();
		if(Boolean.class.equals(clazz))
			return (StringConverter<T>) new BooleanStringConverter();
		if(Class.class.equals(clazz))
			return (StringConverter<T>) new StringConverter<Class<?>>(){

			@Override
			public String toString(Class<?> clazz) {
				return clazz!=null ? clazz.getSimpleName() : null;
			}

			@Override
			public Class<?> fromString(String className) {
				try {
					return Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException(e);
				}
			}
		};
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
					return ((Class<?>)object).getSimpleName();
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