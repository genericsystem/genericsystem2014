package org.genericsystem.exampleswing.application;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ComboBoxEditor extends DefaultCellEditor implements TableCellRenderer {

	private final JComboBox<?> combo;

	public ComboBoxEditor(String[] items) {
		super(new JComboBox(items));
		combo = new JComboBox(items);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			combo.setForeground(table.getSelectionForeground());
			combo.setBackground(table.getSelectionBackground());
		} else {
			combo.setForeground(table.getForeground());
			combo.setBackground(table.getBackground());
		}
		combo.setSelectedItem(value);
		return combo;
	}
}
