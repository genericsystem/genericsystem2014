package org.genericsystem.exampleswing.swingcomponents;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.genericsystem.exampleswing.swingactions.BtnCancelAction;
import org.genericsystem.exampleswing.swingactions.BtnCreateCarAction;
import org.genericsystem.exampleswing.swingactions.BtnFlushAction;
import org.genericsystem.mutability.Engine;

public class CarViewer extends JFrame {
	private static final long serialVersionUID = 5868325769001340979L;

	private Engine engine;

	private JTextField newCar;
	private JTextField newPower;
	private CarTableModel tableModel;

	public CarViewer() {
		setTitle("Cars");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(700, 700));
		JPanel mainPanel = new JPanel();

		tableModel = new CarTableModel(this);
		mainPanel.add(new JScrollPane(new JTable(tableModel)));

		newCar = new JTextField("new car");
		newCar.setColumns(10);
		mainPanel.add(newCar);

		newPower = new JTextField("0");
		newPower.setColumns(10);
		mainPanel.add(newPower);

		mainPanel.add(new JButton(new BtnCreateCarAction(this, "Add car")));

		mainPanel.add(new JButton(new BtnFlushAction(this, "Save")));
		mainPanel.add(new JButton(new BtnCancelAction(this, "Cancel")));

		add(mainPanel);
		setVisible(true);
	}

	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	public Engine getEngine() {
		return engine;
	}

	public JTextField getNewCar() {
		return newCar;
	}

	public JTextField getNewPower() {
		return newPower;
	}
}
