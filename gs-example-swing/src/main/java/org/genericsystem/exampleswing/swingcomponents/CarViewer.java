package org.genericsystem.exampleswing.swingcomponents;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.genericsystem.exampleswing.business.CarBusiness;
import org.genericsystem.exampleswing.swingactions.BtnCancelAction;
import org.genericsystem.exampleswing.swingactions.BtnCreateCarAction;
import org.genericsystem.exampleswing.swingactions.BtnFlushAction;
import org.genericsystem.exampleswing.swingactions.BtnUpdatePowerAction;

public class CarViewer extends JFrame {
	private static final long serialVersionUID = 5868325769001340979L;
	private JTextField newCar;
	private SnapshotTableModel tableModel;

	public CarViewer() {
		setTitle("Cars");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(700, 700));
		JPanel mainPanel = new JPanel();

		List<Serializable> columnsName = new ArrayList<>();
		columnsName.add(Objects.toString(CarBusiness.getInstance().getGeneric().getValue()));

		tableModel = new SnapshotTableModel(CarBusiness.getInstance().getAllInstances(), columnsName);
		JScrollPane scrollPane = new JScrollPane(new JTable(tableModel));
		scrollPane.setLocation(50, 50);
		scrollPane.setSize(new Dimension(500, 500));
		mainPanel.add(scrollPane);

		mainPanel.add(new JButton(new BtnUpdatePowerAction(this, "Update power")));

		newCar = new JTextField("new car");
		newCar.setColumns(10);
		mainPanel.add(newCar);
		mainPanel.add(new JButton(new BtnCreateCarAction(this, "Add car")));

		mainPanel.add(new JButton(new BtnFlushAction("Save")));
		mainPanel.add(new JButton(new BtnCancelAction("Cancel")));

		add(mainPanel);
		setVisible(true);
	}

	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	public JTextField getNewCar() {
		return newCar;
	}

}
