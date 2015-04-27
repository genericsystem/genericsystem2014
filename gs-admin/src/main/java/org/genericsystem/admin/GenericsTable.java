package org.genericsystem.admin;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.genericsystem.admin.AbstractColumn.CheckBoxColumn;
import org.genericsystem.admin.AbstractColumn.DeleteColumn;
import org.genericsystem.admin.AbstractColumn.EditColumn;
import org.genericsystem.admin.AbstractColumn.TargetComponentColumn;
import org.genericsystem.mutability.Generic;


/**
 * @author Nicolas Feybesse
 *
 */
public class GenericsTable extends TableView<Generic>{
	public GenericsTable(Generic type,List<Generic> attributes) {
		setEditable(true);

		TableColumn<Generic, ?> firstColumn = new EditColumn<>(type, g -> g.getValue(), (g, v) -> {
			if(type.getInstance(v)!=null)
				throw new IllegalStateException();
			g.updateValue(v);
		});
		getColumns().add(firstColumn);

		for (Generic attribute : attributes) {
			TableColumn<Generic, ?> column;
			if(attribute.getComponents().size()<2)
				if(Boolean.class.equals(attribute.getClassConstraint()))
					column=new CheckBoxColumn(attribute, g -> (Boolean) g.getValue(attribute), (g, v) -> g.setHolder(attribute, v));
				else
					column= new EditColumn<>(attribute, g -> g.getValue(attribute), (g, v) -> g.setHolder(attribute, v));
			else
				column= new TargetComponentColumn(attribute.getTargetComponent(),g -> g.getLinkTargetComponent(attribute),
						(g, v) -> g.setLink(attribute,null, v));
			getColumns().add(column);
		}
		getColumns().add(new DeleteColumn(type));


		ObservableList<Generic> data = FXCollections.observableArrayList(type.getSubInstances().stream().collect(Collectors.toList()));
		data.addListener((ListChangeListener<Generic>)e->{
			while (e.next()) {
				//e.getAddedSubList().forEach(g->System.out.println("generic : "+g.info()));
				e.getRemoved().forEach(g-> {
					g.remove();
					System.out.println("Remove from GS : "+g.info());
				});
			}
		});
		setItems(data);
	}
}

