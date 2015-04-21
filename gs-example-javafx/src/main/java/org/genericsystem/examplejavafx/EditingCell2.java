//package org.genericsystem.examplejavafx;
//
//import javafx.application.Platform;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.scene.control.cell.TextFieldTableCell;
//import javafx.util.StringConverter;
//
//public class EditingCell2<S,T>  extends TextFieldTableCell<S, T> {
//
//	public EditingCell2(StringConverter<T> converter) {
//		super(converter);
//		setText(converter.toString(getItem()));
//		setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
//
//		focusedProperty().addListener(new ChangeListener<Boolean>() {
//			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//				if(!newValue.booleanValue()) {
//					System.out.println("coucou lost focus : "+getText());
//					Platform.runLater(new Runnable() {
//						@Override
//						public void run() {
//							commitEdit(converter.fromString(getText()));
//						}
//					});
//				}
//			}
//		});
//
////		setOnKeyReleased(new EventHandler<KeyEvent>() {
////			@Override public void handle(KeyEvent t) {
////				if (t.getCode() == KeyCode.ENTER) {
////					commitEdit(converter.fromString(getText()));
////				} else if (t.getCode() == KeyCode.ESCAPE) {
////					cancelEdit();
////				}
////			}
////		});
//	}
//}
