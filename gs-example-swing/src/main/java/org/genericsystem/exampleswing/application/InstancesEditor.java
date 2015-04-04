package org.genericsystem.exampleswing.application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class InstancesEditor extends JFrame {
	private static final long serialVersionUID = 5868325769001340979L;

	private final Engine engine;
	private final Generic type;

	private final JTextField newCarField;
	private final JTextField newPowerField;
	private final InstancesTableModel tableModel;

	public InstancesEditor(Generic type) {
		this.type = type;
		engine = type.getRoot();
		setTitle(Objects.toString(type.getValue()) + "(s)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(700, 700));
		JPanel mainPanel = new JPanel();

		tableModel = new InstancesTableModel(engine.find(Power.class));
		mainPanel.add(new JScrollPane(new JTable(tableModel)));

		newCarField = new JTextField("myAudi");
		newCarField.setColumns(10);
		mainPanel.add(newCarField);

		newPowerField = new JTextField("333");
		newPowerField.setColumns(10);
		mainPanel.add(newPowerField);

		mainPanel.add(new CreateButton("Create"));
		mainPanel.add(new FlushButton("Save"));
		mainPanel.add(new CancelButton("Cancel"));

		add(mainPanel);
		setVisible(true);
	}

	class InstancesTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -8137410628615273305L;

		private Generic[] attributes;

		public InstancesTableModel(Generic... attributes) {
			this.attributes = attributes;
		}

		@Override
		public int getRowCount() {
			return type.getSubInstances().size();
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Objects.toString(type.getValue());
			default:
				return Objects.toString(attributes[columnIndex - 1].getValue());
			}
		}

		@Override
		public int getColumnCount() {
			return attributes.length + 1;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			switch (columnIndex) {
			case 0:
				return Objects.toString(generic.getValue());
			default:
				return Objects.toString(generic.getHolder(attributes[columnIndex - 1]).getValue());
			}
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			switch (columnIndex) {
			case 0:
				generic.updateValue(Objects.toString(value));
				break;
			default:
				generic.setHolder(attributes[columnIndex - 1], Integer.parseInt(Objects.toString(value)));
			}
			fireTableDataChanged();
		}
	}

	class CreateButton extends JButton implements ActionListener {

		private static final long serialVersionUID = -2128684400234426330L;

		CreateButton(String text) {
			super(text);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			engine.find(Car.class).setInstance(newCarField.getText()).setHolder(engine.find(Power.class), Integer.parseInt(newPowerField.getText()));
			tableModel.fireTableDataChanged();
		}
	}

	class FlushButton extends JButton implements ActionListener {

		private static final long serialVersionUID = -8517471002458976396L;

		FlushButton(String text) {
			super(text);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			engine.getCurrentCache().flush();
		}
	}

	class CancelButton extends JButton implements ActionListener {

		private static final long serialVersionUID = -2644092152929305586L;

		CancelButton(String text) {
			super(text);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			engine.getCurrentCache().clear();
			tableModel.fireTableDataChanged();
		}
	}
}
