package org.genericsystem.admin;


import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.genericsystem.admin.model.Car;
import org.genericsystem.admin.model.CarColor;
import org.genericsystem.admin.model.Color;
import org.genericsystem.admin.model.Color.Red;
import org.genericsystem.admin.model.Color.Yellow;
import org.genericsystem.admin.model.Power;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

import com.sun.javafx.collections.ObservableListWrapper;


/**
 * @author Nicolas Feybesse
 *
 */
public class App extends Application {

	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		Scene scene = new Scene(new Group());
		stage.setTitle("Generic System JavaFx Example");

		Engine engine = new Engine(Car.class, Power.class, CarColor.class, Color.class);

		Generic type = engine.find(Car.class);
		Generic base = type.addInstance("myBmw");
		//		type.addInstance("myAudi");
		//		type.addInstance("myMercedes");

		//Crud crud = new Crud(type,type.getAttributes().filter(attribute->type.isInstanceOf(attribute.getComponent(0)))/*.filter(attribute->attribute.isCompositeForInstances(engine))*/.toList());
		//Crud crud = new Crud(type,engine.find(Power.class), engine.find(CarColor.class));

		Generic attribute = engine.find(Power.class);
		Generic relation = engine.find(CarColor.class);
		base.addHolder(attribute, 333);
		base.addLink(relation,"myBmwRed",engine.find(Red.class));
		base.addLink(relation,"myBmwYellow",engine.find(Yellow.class));
		Generic base2 = type.addInstance("myMercedes");
		base2.addLink(relation,"myMercedesYellow",engine.find(Yellow.class));

		Crud crud = new Crud(type);

		((Group) scene.getRoot()).getChildren().add(crud);
		stage.setScene(scene);
		stage.show();
	}


	public static class LinksObservableList extends ObservableListWrapper<Generic> {

		LinksObservableList(Snapshot<Generic> links){
			super(links.toList());
			addListener((ListChangeListener<Generic>)e->{
				while (e.next()) {
					e.getRemoved().forEach(g-> {
						g.remove();
						System.out.println("Remove from GS : "+g.info());
					});
				}
			});
		}
	}
}
