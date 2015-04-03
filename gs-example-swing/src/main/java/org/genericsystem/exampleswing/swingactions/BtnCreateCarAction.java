package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.business.CarBusiness;
import org.genericsystem.exampleswing.swingcomponents.CarViewer;

public class BtnCreateCarAction extends AbstractAction {
	private static final long serialVersionUID = 8200013197094165295L;
	private CarViewer carViewer;

	public BtnCreateCarAction(CarViewer carViewer, String title) {
		super(title);
		this.carViewer = carViewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CarBusiness.getInstance().addCar(carViewer.getNewCar().getText());
		carViewer.refresh();
	}
}
