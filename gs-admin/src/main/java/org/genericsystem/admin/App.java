package org.genericsystem.admin;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.genericsystem.admin.model.Car;
import org.genericsystem.admin.model.CarColor;
import org.genericsystem.admin.model.Color;
import org.genericsystem.admin.model.Power;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;


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
		stage.setWidth(840);
		stage.setHeight(500);
		
		Engine engine = new Engine(Car.class, Power.class, CarColor.class, Color.class);

		Generic type = engine.find(Car.class);
//		type.addInstance("myBmw");
//		type.addInstance("myAudi");
//		type.addInstance("myMercedes");
		
		Crud crud = new Crud(type,type.getAttributes().filter(attribute->type.inheritsFrom(attribute.getComponent(ApiStatics.BASE_POSITION)))/*.filter(attribute->attribute.isCompositeForInstances(engine))*/.toList());
		//Crud crud = new Crud(type,type.getAttributes().filter(attribute->type.isInstanceOf(attribute.getComponent(0)))/*.filter(attribute->attribute.isCompositeForInstances(engine))*/.toList());
		//Crud crud = new Crud(type,engine.find(Power.class), engine.find(CarColor.class));
		
		((Group) scene.getRoot()).getChildren().addAll(crud);
		stage.setScene(scene);
		stage.show();
	}
}
