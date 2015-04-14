package org.genericsystem.exampleswing.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.genericsystem.exampleswing.application.CacheManager.Refreshable;
import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.model.CarColor;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class InstancesEditor extends JFrame implements Refreshable {
	private static final long serialVersionUID = 5868325769001340979L;

	private final Engine engine;
	private final Generic type;

	private final InstancesTableModel tableModel;

	private static final int INSTANCE_INDEX = 0;
	private static final int CAR_COLOR_INDEX = 2;
	private static final int DELETE_INDEX = 3;

	public InstancesEditor(Generic type) {
		this.type = type;
		engine = type.getRoot();
		setTitle(Objects.toString(type.getValue()) + "(s) Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTable table = new JTable(tableModel = new InstancesTableModel(engine.find(Power.class), engine.find(CarColor.class)));
		table.setColumnModel(adjustColumnEditor(table));

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(new CreatePanel(), BorderLayout.EAST);
		getContentPane().add(new CacheManager(engine, this), BorderLayout.SOUTH);

		getContentPane().setPreferredSize(new Dimension(700, 700));
		pack();
		setVisible(true);
	}

	private TableColumnModel adjustColumnEditor(JTable table) {
		int indexAttribute = 0;
		int indexColumn = 1;
		for (; indexColumn < table.getColumnModel().getColumnCount() - 1; indexColumn++) {
			TableColumn tableColumn = table.getColumnModel().getColumn(indexColumn);
			tableColumn.setCellEditor(getEditor(tableModel.attributes[indexAttribute]));
			tableColumn.setCellRenderer(getRenderer(tableModel.attributes[indexAttribute]));
			indexAttribute++;
		}
		TableColumn column = table.getColumnModel().getColumn(indexColumn);
		ButtonEditor buttonColumn = new ButtonEditor(table, indexColumn);
		column.setCellRenderer(buttonColumn);
		column.setCellEditor(buttonColumn);
		return table.getColumnModel();
	}

	private TableCellRenderer getRenderer(Generic attribute) {
		if (!isAssociation(attribute))
			return null;
		return new ComboBoxEditor(new String[] { "Red", "Green" });
	}

	private TableCellEditor getEditor(Generic attribute) {
		if (!isAssociation(attribute))
			return null;
		return new ComboBoxEditor(new String[] { "Red", "Green" });
	}

	private boolean isAssociation(Generic attribute) {
		return attribute.getComponents().size() == 2;
	}

	@Override
	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	private class CreatePanel extends JPanel {

		private static final long serialVersionUID = 2790743754106657404L;
		private final JTextField newCarField;
		private final JTextField newPowerField;

		CreatePanel() {
			newCarField = new JTextField("myAudi");
			newCarField.setColumns(10);
			add(newCarField);
			newPowerField = new JTextField("333");
			newPowerField.setColumns(10);
			add(newPowerField);
			add(new CreateButton("Create"));
		}

		private class CreateButton extends JButton implements ActionListener {

			private static final long serialVersionUID = -9204494808782375894L;

			public CreateButton(String text) {
				super(text);
				addActionListener(this);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				engine.find(Car.class).setInstance(newCarField.getText()).setHolder(engine.find(Power.class), Integer.parseInt(newPowerField.getText()));
				tableModel.fireTableDataChanged();
			}
		}
	}

	private class InstancesTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -8137410628615273305L;

		private final Generic[] attributes;

		public InstancesTableModel(Generic... attributes) {
			this.attributes = attributes;
		}

		@Override
		public int getRowCount() {
			return type.getSubInstances().size();
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == INSTANCE_INDEX)
				return Objects.toString(type.getValue());
			if (columnIndex == DELETE_INDEX)
				return "Delete";
			return Objects.toString(attributes[columnIndex - 1].getValue());
		}

		@Override
		public int getColumnCount() {
			return attributes.length + 2;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == INSTANCE_INDEX)
				return Objects.toString(generic.getValue());
			if (columnIndex == DELETE_INDEX)
				return "Delete";
			return Objects.toString(generic.getValue(attributes[columnIndex - 1]));
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == INSTANCE_INDEX)
				generic.updateValue(Objects.toString(value));
			else if (columnIndex == DELETE_INDEX) {
				int returnCode = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(InstancesEditor.this), "Are you sure you want to delete generic : " + generic.info());
				if (JOptionPane.OK_OPTION == returnCode)
					generic.remove();
			} else
				generic.setHolder(attributes[columnIndex - 1], Integer.parseInt(Objects.toString(value)));
			fireTableDataChanged();
		}
	}
}
