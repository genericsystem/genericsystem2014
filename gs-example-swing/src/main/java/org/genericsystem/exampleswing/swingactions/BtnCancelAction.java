package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.swingcomponents.CarViewer;

public class BtnCancelAction extends AbstractAction {
	private static final long serialVersionUID = 4864122081521896909L;

	private CarViewer carViewer;

	public BtnCancelAction(CarViewer carViewer, String title) {
		super(title);
		this.carViewer = carViewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		carViewer.getEngine().getCurrentCache().clear();
	}
}
