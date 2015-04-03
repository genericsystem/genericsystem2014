package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.exampleswing.swingcomponents.CarViewer;
import org.genericsystem.mutability.Generic;

public class BtnCreateCarAction extends AbstractAction {
	private static final long serialVersionUID = 8200013197094165295L;
	private CarViewer carViewer;

	public BtnCreateCarAction(CarViewer carViewer, String title) {
		super(title);
		this.carViewer = carViewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Generic newCar = carViewer.getEngine().find(Car.class).setInstance(carViewer.getNewCar().getText());
		newCar.setHolder(carViewer.getEngine().find(Power.class), Integer.parseInt(carViewer.getNewPower().getText()));
		carViewer.refresh();
	}
}
