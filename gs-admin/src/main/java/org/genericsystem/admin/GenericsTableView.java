package org.genericsystem.admin;


import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.genericsystem.admin.AbstractColumn.DeleteColumn;
import org.genericsystem.admin.AbstractColumn.EditColumn;
import org.genericsystem.admin.AbstractColumn.GenericComponentColumn;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.mutability.Generic;


/**
 * @author Nicolas Feybesse
 *
 */
public class GenericsTableView extends TableView<Generic>{
	public GenericsTableView(Generic type) {
		//setPrefWidth(1800);
		//setPrefWidth(1580);
		
		TableColumn<Generic, ?> firstColumn = new EditColumn<>(type, g -> g.getValue(), (g, v) -> {
			if(type.getInstance(v)!=null)
				throw new IllegalStateException();
			g.updateValue(v);
		});
		getColumns().add(firstColumn);

		
		for (Generic attribute : type.getAttributes().filter(attribute->type.inheritsFrom(attribute.getComponent(ApiStatics.BASE_POSITION)))) {
				getColumns().add(new GenericComponentColumn(attribute,attribute.getComponents().indexOf(type),g -> g.getHolders(attribute), null));
		}
		getColumns().add(new DeleteColumn(type));
		setEditable(true);



		ObservableList<Generic> data = FXCollections.observableArrayList(type.getSubInstances().stream().collect(Collectors.toList()));
		data.addListener((ListChangeListener<Generic>)e->{
			while (e.next()) {
				e.getRemoved().forEach(g-> {
					g.remove();
					System.out.println("Remove from GS : "+g.info());
				});
			}
		});
		setItems(data);
//		setFixedCellSize(100);
//	    prefHeightProperty().bind(fixedCellSizeProperty().multiply(Bindings.size(getItems()).add(1.01)));
//	    minHeightProperty().bind(prefHeightProperty());
//	    maxHeightProperty().bind(prefHeightProperty());

	}
}

