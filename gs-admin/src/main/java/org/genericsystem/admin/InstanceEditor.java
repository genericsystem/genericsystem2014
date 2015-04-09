package org.genericsystem.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.genericsystem.admin.CacheManager.Refreshable;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class InstanceEditor extends JFrame implements Refreshable {
	private static final long serialVersionUID = 5868325769001340979L;

	private final Engine engine;

	private final InstanceTableModel tableModel;

	public InstanceEditor(Generic instance) {
		engine = instance.getRoot();
		setTitle(Objects.toString(instance.getValue()));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tableModel = new InstanceTableModel(instance);
		JTable table = new JTable(tableModel);
		JScrollPane scrollTable = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		getContentPane().add(scrollTable, BorderLayout.CENTER);
		getContentPane().add(new CacheManager(engine, this), BorderLayout.SOUTH);

		getContentPane().setPreferredSize(new Dimension(700, 700));
		pack();
		setVisible(true);

	}

	@Override
	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	private class InstanceTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -8137410628615273305L;

		private final Generic instance;

		public InstanceTableModel(Generic instance) {
			this.instance = instance;
		}

		@Override
		public int getRowCount() {
			return instance.getAttributes().size();
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnIndex == 0 ? "Attribut(s)" : "Value(s)";
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO KK rowIndex
			return columnIndex == 1 && rowIndex == 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Generic attribute = instance.getAttributes().getByIndex(rowIndex);
			if (columnIndex == 0)
				return Objects.toString(attribute.getValue());
			Generic holder = instance.getHolder(attribute);
			return holder != null ? Objects.toString(holder.getValue()) : null;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			assert columnIndex == 1;
			instance.setHolder(instance.getAttributes().getByIndex(rowIndex), Integer.parseInt(Objects.toString(value)));
			fireTableDataChanged();
		}
	}
}
