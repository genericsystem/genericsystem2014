package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.swingcomponents.CarViewer;

public class BtnFlushAction extends AbstractAction {
	private static final long serialVersionUID = -280644517749216140L;

	private CarViewer carViewer;

	public BtnFlushAction(CarViewer carViewer, String title) {
		super(title);
		this.carViewer = carViewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		carViewer.getEngine().getCurrentCache().flush();
	}
}
