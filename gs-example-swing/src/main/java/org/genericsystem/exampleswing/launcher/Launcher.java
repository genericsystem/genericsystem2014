package org.genericsystem.exampleswing.launcher;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.swingcomponents.SnapshotTableModel;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Launcher {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame window = new JFrame();

				window.setTitle("gs-example-swing");
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.setSize(new Dimension(700, 700));

				JPanel mainPanel = new JPanel();
				window.add(mainPanel);

				JLabel hello = new JLabel("Hello !");
				mainPanel.add(hello);

				// Object[][] rowData = { { "FirstCar", "FirstPower" }, { "SecondCar", "SecondPower" } };
				Object[] columnNames = { "Car" };
				Engine engine = new Engine(Car.class);
				engine.newCache().start();
				// Root root = new Root(Car.class);
				Generic car = engine.find(Car.class);
				car.addInstance("myBmw");
				car.addInstance("myAudi");
				car.addInstance("myMercedes");
				Snapshot<Generic> snapshotData = car.getSubInstances();
				System.out.println("snapshot : " + snapshotData.size());
				for (Generic generic : snapshotData) {
					System.out.println("Snapshot contains " + generic.getValue());
				}

				TableModel tableModel = new SnapshotTableModel(snapshotData, columnNames);
				JTable table = new JTable(tableModel);
				JScrollPane scrollPane = new JScrollPane(table);
				scrollPane.setLocation(50, 50);
				scrollPane.setSize(new Dimension(500, 500));
				mainPanel.add(scrollPane);

				window.setVisible(true);

			}
		});

	}
}
