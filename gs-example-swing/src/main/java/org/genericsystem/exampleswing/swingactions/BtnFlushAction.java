package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.genericsystem.exampleswing.business.CarBusiness;

public class BtnFlushAction extends AbstractAction {
	private static final long serialVersionUID = -280644517749216140L;

	public BtnFlushAction(String title) {
		super(title);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CarBusiness.getInstance().flush();
	}
}
