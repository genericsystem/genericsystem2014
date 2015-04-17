package org.genericsystem.examplejavafx;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

import org.genericsystem.examplejavafx.model.Car;
import org.genericsystem.examplejavafx.model.CarColor;
import org.genericsystem.examplejavafx.model.Color;
import org.genericsystem.examplejavafx.model.Power;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

import com.sun.javafx.scene.control.skin.VirtualFlow;

public class App extends Application {

	private final TableView<Generic> table = new TableView<>();
	private final Engine engine = new Engine(Car.class, Power.class, CarColor.class, Color.class);

	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Generic type = engine.find(Car.class);
		Generic[] attributes = new Generic[] { engine.find(Power.class), engine.find(CarColor.class) };

		Scene scene = new Scene(new Group());
		stage.setTitle("Generic System JavaFx Example");
		stage.setWidth(840);
		stage.setHeight(500);

		table.setEditable(true);


		TableColumn<Generic, String> firstColumn = buildColumn(String.class,Objects.toString(type.getValue()), 200, g -> Objects.toString(g.getValue()), (g, v) -> g.updateValue(v));
		table.getColumns().add(firstColumn);

		for (Generic attribute : attributes) {
			TableColumn<Generic, ? extends Serializable> column = buildColumn((Class<? extends Serializable>)attribute.getClassConstraint(),Objects.toString(attribute.getValue()), 200, g -> g.getValue(attribute), (g, v) -> g.setHolder(attribute, v));
			table.getColumns().add(column);
		}

		TableColumn<Generic, Generic> lastColumn =new TableColumn<>("Delete");
		lastColumn.setMinWidth(200);
		lastColumn.setCellValueFactory(new Callback<CellDataFeatures<Generic, Generic>, ObservableValue<Generic>>() {
			@Override public ObservableValue<Generic> call(CellDataFeatures<Generic, Generic> features) {
				return new ReadOnlyObjectWrapper<>(features.getValue());
			}
		});
		lastColumn.setEditable(true);
		lastColumn.setMinWidth(200);

		lastColumn.setCellFactory(
				new Callback<TableColumn<Generic, Generic>, TableCell<Generic, Generic>>() {
					@Override
					public TableCell<Generic, Generic> call(TableColumn<Generic, Generic> p) {
						return new ButtonCell();
					}

				});
		table.getColumns().add(lastColumn);


		ObservableList<Generic> data = FXCollections.observableArrayList(type.getInstances().get().collect(Collectors.toList()));
		table.setItems(data);

		HBox hb = new HBox();
		final TextField newTextFild = new TextField();
		newTextFild.setMaxWidth(200);


		final Button addButton = new Button("Add");
		addButton.setOnAction((ActionEvent e) -> {
			Generic generic = type.setInstance(newTextFild.getText());
			if(!data.contains(generic))
				data.add(generic);
		});
		hb.getChildren().addAll(newTextFild,addButton);
		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(table,hb);

		((Group) scene.getRoot()).getChildren().addAll(vbox);

		stage.setScene(scene);
		stage.show();
	}

	private class ButtonCell extends TableCell<Generic, Generic> {
		private final Button cellButton = new Button();

		private ButtonCell(){
			cellButton.setMaxWidth(200);
			cellButton.setAlignment(Pos.BASELINE_LEFT);
		}

		@Override
		protected void updateItem(Generic t, boolean empty) {
			super.updateItem(t, empty);
			if(empty || t==null){
				cellButton.setText(null);
				setGraphic(null);
			}else {
				cellButton.setText("Delete : "+Objects.toString(t.getValue()));
				setGraphic(cellButton);
				cellButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override 
					public void handle(ActionEvent event) {
						t.remove();
						table.getItems().remove(t);
						System.out.println("Remove : "+t.info());
					}
				});

			}
		}
	}

	<T> TableColumn<Generic, T> buildColumn(Class<T> clazz,String columnName, int minWidth, Function<Generic, T> getter, BiConsumer<Generic, T> setter) {
		if(Integer.class.equals(clazz))
			return (TableColumn)new IntegerColumn(columnName,minWidth,(Function)getter,(BiConsumer)setter);
		return (TableColumn)new StringColumn(columnName,minWidth,(Function)getter,(BiConsumer)setter);

	}


	private abstract class AbstractGenericColumn<T> extends TableColumn<Generic,T> {

		private AbstractGenericColumn(String columnName, int minWidth,Function<Generic, T> getter,BiConsumer<Generic, T> setter) {
			super(columnName);
			setMinWidth(minWidth);

			setCellValueFactory(cellData -> getObservable(cellData.getValue(),getter.apply(cellData.getValue())));
			setOnEditCommit((CellEditEvent<Generic, T> t) -> {
				System.out.println("coucou");
				setter.accept((t.getTableView().getItems().get(t.getTablePosition().getRow())), t.getNewValue());
			});
			setCellFactory(list -> getCellFactory(getConverter()));
			//setCellFactory(list -> new TextFieldTableCell<Generic,T>(getConverter()));
			// column.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList("1", "2", "3")));
			setEditable(true);	
		}


		ObservableValue<T> getObservable(Generic generic,T value) {
			return new SimpleObjectProperty<T>(value);
		}

		abstract StringConverter<T> getConverter();

		abstract TableCell<Generic,T> getCellFactory(StringConverter<T> converter);

	}


	private class StringColumn extends AbstractGenericColumn<String>{


		private StringColumn(String columnName, int minWidth, Function<Generic, String> getter, BiConsumer<Generic, String> setter) {
			super(columnName, minWidth,getter, setter);
		}

		@Override
		StringConverter<String> getConverter() {
			return new DefaultStringConverter();
		}

		@Override
		TableCell<Generic, String> getCellFactory(StringConverter<String> converter) {
			return  new EditingCell<String>(converter);
		}
	}

	private class IntegerColumn extends AbstractGenericColumn<Integer>{
		private IntegerColumn(String columnName, int minWidth, Function<Generic, Integer> getter, BiConsumer<Generic, Integer> setter) {
			super(columnName, minWidth,getter, setter);
		}


		@Override
		StringConverter<Integer> getConverter() {
			return new IntegerStringConverter();
		}

		@Override
		TableCell<Generic, Integer> getCellFactory(StringConverter<Integer> converter) {
			return new EditingCell<Integer>(converter);
		}


	}

		static class EditingCell<T> extends TableCell<Generic, T> {
			 
	        private TextField textField;
	        private StringConverter<T> converter;
	        
	        public EditingCell(StringConverter<T> converter) {
				this.converter = converter;
			}
	        
	       
	        @Override
	        public void startEdit() {
	            if (!isEmpty()) {
	                super.startEdit();
	                createTextField();
	                setText(null);
	                setGraphic(textField);
	                textField.selectAll();
	            }
	        }
	 
	        @Override
	        public void cancelEdit() {
	            super.cancelEdit();
	 
	            setText(converter.toString(getItem()));
	            setGraphic(null);
	        }
	 
	        @Override
	        public void updateItem(T item, boolean empty) {
	            super.updateItem(item, empty);
	 
	            if (empty) {
	                setText(null);
	                setGraphic(null);
	            } else {
	                if (isEditing()) {
	                    if (textField != null) {
	                        textField.setText(converter.toString(getItem()));
	                    }
	                    setText(null);
	                    setGraphic(textField);
	                } else {
	                    setText(converter.toString(getItem()));
	                    setGraphic(null);
	                }
	            }
	        }
	 
	        private void createTextField() {
	            textField = new TextField(converter.toString(getItem()));
	            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
  
	            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	                    if(!newValue.booleanValue() && textField != null)
	                        commitEdit(converter.fromString(textField.getText()));
	                }
	            } );
	           
	            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
	                @Override public void handle(KeyEvent t) {
	                    if (t.getCode() == KeyCode.ENTER) {
	                        commitEdit(converter.fromString(textField.getText()));
	                    } else if (t.getCode() == KeyCode.ESCAPE) {
	                        cancelEdit();
	                    }
	                }
	            });
	        }
	    }

	public interface TextColumnValidator<T> {
		boolean valid(Generic rowVal, T newVal);
	}

	public static class TextFieldTableCellEx<T> extends TextFieldTableCell<Generic, T> {

		//private final StringConverter<T> converter;
		private final TextColumnValidator<T> validator;

		private boolean cancelling;
		private boolean hardCancel;
		private String curTxt = "";

		//	    public  Callback<TableColumn<S, String>, TableCell<S, String>>
		//	        cellFactory(final TextColumnValidator<S> validator) {
		//	            return new Callback<TableColumn<S, String>, TableCell<S, String>>() {
		//	                @Override public TableCell<S, String> call(TableColumn<S, String> col) {
		//	                    return new TextFieldTableCellEx<>(converter,validator);
		//	                }
		//	            };
		//	    }

		private TextFieldTableCellEx(StringConverter<T> converter,TextColumnValidator<T> validator) {
			super(converter);
			//this.converter = converter;
			this.validator = validator;
		}

		@Override
		public void startEdit() {
			super.startEdit();

			curTxt = "";

			hardCancel = false;

			Node g = getGraphic();

			if (g != null) {
				final TextField tf = (TextField)g;

				tf.textProperty().addListener(new ChangeListener<String>() {
					@Override public void changed(ObservableValue<? extends String> val, String oldVal, String newVal) {
						curTxt = newVal;
					}
				});

				tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
					@Override public void handle(KeyEvent evt) {
						if (KeyCode.ENTER == evt.getCode())
							cancelEdit();
						else if (KeyCode.ESCAPE == evt.getCode()) {
							hardCancel = true;

							cancelEdit();
						}
					}
				});

				// Special hack for editable TextFieldTableCell.
				// Cancel edit when focus lost from text field, but do not cancel if focus lost to VirtualFlow.
				tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
					@Override public void changed(ObservableValue<? extends Boolean> val, Boolean oldVal, Boolean newVal) {
						Node fo = getScene().getFocusOwner();

						if (!newVal) {
							if (fo instanceof VirtualFlow) {
								if (fo.getParent().getParent() != getTableView())
									cancelEdit();
							}
							else
								cancelEdit();
						}
					}
				});

				Platform.runLater(new Runnable() {
					@Override public void run() {
						tf.requestFocus();
					}
				});
			}
		}

		@Override 
		public void cancelEdit() {
			if (cancelling)
				super.cancelEdit();
			else
				try {
					cancelling = true;

					if (hardCancel || curTxt.trim().isEmpty())
						super.cancelEdit();
					else if (validator.valid(getTableView().getSelectionModel().getSelectedItem(), getConverter().fromString(curTxt)))
						commitEdit(getConverter().fromString(curTxt));
					else
						super.cancelEdit();
				}
			finally {
				cancelling = false;
			}
		}
	}


}
