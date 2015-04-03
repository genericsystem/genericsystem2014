package org.genericsystem.exampleswing.launcher;

import javax.swing.SwingUtilities;

import org.genericsystem.exampleswing.swingcomponents.CarViewer;

public class Launcher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CarViewer();
			}
		});
	}
}
