package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.swingcomponents.CarViewer;

public class BtnUpdatePowerAction extends AbstractAction {
	private static final long serialVersionUID = 8770119993010321136L;
	private CarViewer carViewer;

	public BtnUpdatePowerAction(CarViewer carViewer, String title) {
		super(title);
		this.carViewer = carViewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("BtnUpdatePowerAction");
		// String newPowerString = snapshotViewer.getNewPower().getText();
		// Integer newPowerValue = 0;
		// try {
		// newPowerValue = Integer.parseInt(newPowerString);
		// } catch (NumberFormatException exception) {
		// exception.printStackTrace();
		// }
		// System.out.println(newPowerValue);
		// TODO update powers

	}

}
