package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.business.CarBusiness;

public class BtnCancelAction extends AbstractAction {
	private static final long serialVersionUID = 4864122081521896909L;

	public BtnCancelAction(String title) {
		super(title);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CarBusiness.getInstance().cancel();
	}
}
