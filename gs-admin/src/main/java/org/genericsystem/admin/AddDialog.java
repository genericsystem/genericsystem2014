package org.genericsystem.admin;

import java.io.Serializable;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.genericsystem.mutability.Generic;

public class AddDialog extends Dialog<Generic> {
	public AddDialog(Generic instance, Generic type, ObservableList<Generic> tableItems, String title, String headerText) {
		setTitle(title);
		setHeaderText(headerText);
		setResizable(true);

		Label label1 = new Label("Value : ");
		Label label2 = new Label("Other : ");
		TextField text1 = new TextField();
		TextField text2 = new TextField();

		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(text1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(text2, 2, 2);
		getDialogPane().setContent(grid);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		setResultConverter(buttonType -> {
			if (buttonType == ButtonType.OK) {
				Generic generic = instance != null ? type.addInstance(AbstractColumn.<Serializable> getDefaultConverter(type).fromString(text1.getText()), instance) : type.setInstance(text1.getText());
				if (!tableItems.contains(generic))
					tableItems.add(generic);
				return generic;
			}
			return null;
		});
	}
}