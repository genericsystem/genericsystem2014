package org.genericsystem.exampleswing.swingcomponents;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.exampleswing.swingactions.BtnUpdatePowerAction;
import org.genericsystem.mutability.Generic;

public class SnapshotViewer extends JFrame {
	private static final long serialVersionUID = 5868325769001340979L;

	public SnapshotViewer(Snapshot<Generic> snapshot) {
		this.setTitle("gs-example-swing");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(700, 700));
		JPanel mainPanel = new JPanel();

		JLabel hello = new JLabel("Hello !");
		mainPanel.add(hello);

		Serializable column1 = Objects.toString(snapshot.first().getMeta().getValue());
		Serializable column2 = snapshot.first().getAttributes().first().getValue();
		Object[] columnsName = { column1, column2 };

		TableModel tableModel = new SnapshotTableModel(snapshot, columnsName);
		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setLocation(50, 50);
		scrollPane.setSize(new Dimension(500, 500));

		mainPanel.add(scrollPane);

		JTextField newPower = new JTextField();
		newPower.setColumns(10);
		mainPanel.add(newPower);

		JButton btnUpdatePower = new JButton(new BtnUpdatePowerAction("Update power"));
		mainPanel.add(btnUpdatePower);

		this.add(mainPanel);

		this.setVisible(true);
	}
}
