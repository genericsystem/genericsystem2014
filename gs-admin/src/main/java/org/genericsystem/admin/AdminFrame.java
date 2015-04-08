package org.genericsystem.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.genericsystem.admin.CacheManager.Refreshable;

public class AdminFrame extends JFrame implements Refreshable {
	private static final long serialVersionUID = 5868325769001340979L;

	EngineManager engineManager;
	AdminPanel adminPanel;

	public AdminFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Admin GS");
		engineManager = new EngineManager(this);
		getContentPane().add(engineManager, BorderLayout.NORTH);
		getContentPane().add(new ConsoleManager(), BorderLayout.SOUTH);

		getContentPane().setPreferredSize(new Dimension(700, 900));
		pack();
		setVisible(true);
	}

	@Override
	public void refresh() {
		if (adminPanel == null)
			getContentPane().add(new AdminPanel(), BorderLayout.CENTER);
	}

	class AdminPanel extends JPanel implements Refreshable {
		private static final long serialVersionUID = 3586350333741588123L;

		private CacheManager cacheManager;
		private InstancesManager instancesManager;

		public AdminPanel() {
			setLayout(new BorderLayout());
			if (cacheManager == null) {
				cacheManager = new CacheManager(engineManager.getEngine(), AdminFrame.this);
				add(cacheManager, BorderLayout.NORTH);
			}
			if (instancesManager == null) {
				instancesManager = new InstancesManager(engineManager.getEngine());
				add(instancesManager, BorderLayout.CENTER);
			}
		}

		@Override
		public void refresh() {
			if (engineManager.getEngine() != null) {
				getContentPane().add(new AdminPanel(), BorderLayout.CENTER);
				instancesManager.setVisible(true);
				cacheManager.setVisible(true);
			} else {
				instancesManager.setVisible(false);
				cacheManager.setVisible(false);
			}
		}
	}
}
