package org.genericsystem.exampleswing.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
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

	public InstancesEditor(Generic type) {
		this.type = type;
		engine = type.getRoot();
		setTitle(Objects.toString(type.getValue()) + "(s) Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Generic[] attributes = new Generic[] { engine.find(Power.class), engine.find(CarColor.class) };
		TableColumnModel columnModel = buildColumnModel(attributes);

		columnModel.addColumn(new TableColumn(columnModel.getColumnCount()));
		TableColumn column = columnModel.getColumn(columnModel.getColumnCount() - 1);
		column.setHeaderValue("Delete");
		ButtonEditor buttonEditor = new ButtonEditor(columnModel, columnModel.getColumnCount() - 1);
		column.setCellRenderer(buttonEditor);
		column.setCellEditor(buttonEditor);

		tableModel = new InstancesTableModel(columnModel);
		JTable table = new JTable(tableModel, columnModel);
		table.addMouseListener(buttonEditor);

		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		getContentPane().add(new CreatePanel(), BorderLayout.EAST);
		getContentPane().add(new CacheManager(engine, this), BorderLayout.SOUTH);

		getContentPane().setPreferredSize(new Dimension(700, 700));
		pack();
		setVisible(true);
	}

	private TableColumnModel buildColumnModel(Generic[] attributes) {
		TableColumnModel columnModel = new DefaultTableColumnModel();
		int indexColumn = 1;
		columnModel.addColumn(new TableColumn(0));
		columnModel.getColumn(0).setHeaderValue(type.getValue());
		for (Generic attribute : attributes) {
			columnModel.addColumn(new GenericColumn(attribute, indexColumn));
			TableColumn tableColumn = columnModel.getColumn(indexColumn);
			tableColumn.setCellEditor(getEditor(attribute));
			tableColumn.setCellRenderer(getRenderer(attribute));
			tableColumn.setHeaderValue(attribute.getValue());
			indexColumn++;
		}
		return columnModel;
	}

	private static TableCellRenderer getRenderer(Generic attribute) {
		if (!isAssociation(attribute))
			return null;
		return new ComboBoxEditor(attribute.getTargetComponent().getInstances().get().map(x -> (String) x.getValue()).collect(Collectors.toList()));
	}

	private static TableCellEditor getEditor(Generic attribute) {
		if (!isAssociation(attribute))
			return null;
		return new ComboBoxEditor(attribute.getTargetComponent().getInstances().get().map(x -> (String) x.getValue()).collect(Collectors.toList()));
	}

	private static boolean isAssociation(Generic attribute) {
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

		private final TableColumnModel columnModel;

		public InstancesTableModel(TableColumnModel columnModel) {
			this.columnModel = columnModel;
		}

		@Override
		public int getRowCount() {
			return type.getSubInstances().size();
		}

		@Override
		public int getColumnCount() {
			return columnModel.getColumnCount();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == 0)
				return Objects.toString(generic.getValue());
			if (columnIndex == getColumnCount() - 1)
				return "Delete";
			return ((GenericColumn) columnModel.getColumn(columnIndex)).getValue(rowIndex);
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == 0)
				generic.updateValue(Objects.toString(value));
			else if (columnIndex == getColumnCount() - 1) {
				int returnCode = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(InstancesEditor.this), "Are you sure you want to delete generic : " + generic.info());
				if (JOptionPane.OK_OPTION == returnCode)
					generic.remove();
			} else
				((GenericColumn) columnModel.getColumn(columnIndex)).setValue(value, rowIndex);
			fireTableDataChanged();
		}
	}

	private class GenericColumn extends TableColumn {
		private static final long serialVersionUID = -6057364790878771041L;

		private final Generic attribute;

		public GenericColumn(Generic attribute, int modelIndex) {
			super(modelIndex);
			this.attribute = attribute;
		}

		public Serializable getValue(int rowIndex) {
			if (InstancesEditor.isAssociation(attribute))
				return type.getSubInstances().getByIndex(rowIndex).getLink(attribute).getTargetComponent().getValue();
			return type.getSubInstances().getByIndex(rowIndex).getValue(attribute);
		}

		public void setValue(Object value, int rowIndex) {
			Generic instance = type.getSubInstances().getByIndex(rowIndex);
			Class<?> classConstraint = attribute.getClassConstraint();
			if (classConstraint != null && Integer.class.isAssignableFrom(classConstraint))
				instance.setHolder(attribute, Integer.parseInt(Objects.toString(value)));
			else if (classConstraint == null) {
				if (!InstancesEditor.isAssociation(attribute))
					instance.setHolder(attribute, Objects.toString(value));
				else
					instance.setLink(attribute, null, attribute.getTargetComponent().getInstance(Objects.toString(value)));
			}
		}

	}
}
