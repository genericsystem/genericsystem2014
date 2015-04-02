package org.genericsystem.exampleswing.swingactions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class BtnUpdatePowerAction extends AbstractAction {
	private static final long serialVersionUID = 8770119993010321136L;

	public BtnUpdatePowerAction(String texte) {
		super(texte);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("BtnUpdatePowerAction");
		// TODO update powers

	}

}
