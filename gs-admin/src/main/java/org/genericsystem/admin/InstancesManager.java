package org.genericsystem.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.genericsystem.admin.CacheManager.Refreshable;
import org.genericsystem.mutability.Generic;

public class InstancesManager extends JPanel implements Refreshable {
	private static final long serialVersionUID = 5868325769001340979L;

	private final InstancesTableModel tableModel;

	InstancesManager(Generic type) {
		setPreferredSize(new Dimension(500, 500));

		tableModel = new InstancesTableModel(type);
		JTable table = new JTable(tableModel);
		table.setFillsViewportHeight(true);

		table.getColumn("Delete").setCellRenderer(new JTableButtonRenderer());
		table.addMouseListener(new JTableButtonMouseListener(table));

		add(new JScrollPane(table), BorderLayout.CENTER);
		add(new CreatePanel(type), BorderLayout.EAST);
	}

	@Override
	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	private class CreatePanel extends JPanel {

		private static final long serialVersionUID = 2790743754106657404L;
		private final JTextField newInstanceField;

		private final Generic type;

		CreatePanel(Generic type) {
			this.type = type;
			newInstanceField = new JTextField("new Instance");
			newInstanceField.setColumns(10);
			add(newInstanceField);
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
				type.setInstance(newInstanceField.getText());
				tableModel.fireTableDataChanged();
			}
		}
	}

	private class InstancesTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -8137410628615273305L;

		private final Generic type;

		public InstancesTableModel(Generic type) {
			this.type = type;
		}

		@Override
		public int getRowCount() {
			return type.getSubInstances().size();
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0)
				return Objects.toString(type.getValue());
			if (columnIndex == getColumnCount() - 1)
				return "Delete";
			return Objects.toString(type.getAttributes().getByIndex(columnIndex - 1).getValue());
		}

		@Override
		public int getColumnCount() {
			return type.getAttributes().size() + 2;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex != getColumnCount() - 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == 0)
				return Objects.toString(generic.getValue());
			if (columnIndex == getColumnCount() - 1) {
				JButton button = new JButton("Delete");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int returnCode = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(button), "Are you sure you want to delete generic : " + generic.info());
						if (JOptionPane.OK_OPTION == returnCode) {
							generic.remove();
							fireTableDataChanged();
						}
					}
				});
				return button;
			}
			Generic attribute = type.getAttributes().getByIndex(columnIndex);
			if (attribute == null)
				return null;
			Generic holder = generic.getHolder(attribute);
			return holder == null ? null : Objects.toString(holder.getValue());
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == 0) {
				generic.updateValue(Objects.toString(value));
				return;
			}
			assert columnIndex != getColumnCount() - 1;
			// generic.setHolder(type.getAttributes().getByIndex(columnIndex - 1), Integer.parseInt(Objects.toString(value)));
			fireTableDataChanged();
		}
	}

	private static class JTableButtonRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JButton button = (JButton) value;
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(UIManager.getColor("Button.background"));
			}
			return button;
		}
	}

	private static class JTableButtonMouseListener extends MouseAdapter {
		private final JTable table;

		public JTableButtonMouseListener(JTable table) {
			this.table = table;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int column = table.getColumnModel().getColumnIndexAtX(e.getX());
			int row = e.getY() / table.getRowHeight();

			if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
				Object value = table.getValueAt(row, column);
				if (value instanceof JButton)
					((JButton) value).doClick();
			}
		}
	}
}
