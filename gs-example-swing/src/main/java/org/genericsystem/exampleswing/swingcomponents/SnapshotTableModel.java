package org.genericsystem.exampleswing.swingcomponents;

import javax.swing.table.AbstractTableModel;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Generic;

public class SnapshotTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -8137410628615273305L;

	// protected Object[][] datatable;
	protected Snapshot<Generic> snapshot;
	protected Object[] columnsIdentifiers;

	// TODO
	// public MyTableModel() {
	// this(new Snapshot<Generic>[0], new Object[0]);
	// }

	public SnapshotTableModel(Snapshot<Generic> datatable, Object[] columnsIdentifiers) {
		this.snapshot = datatable;
		this.columnsIdentifiers = columnsIdentifiers;
	}

	public int getRowCount() {
		return snapshot.size();
	}

	public int getColumnCount() {
		return columnsIdentifiers.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		for (int i = 0; i < snapshot.size(); i++) {
			if (i == rowIndex)
				return snapshot.getByIndex(i).getValue();
			// for (int j = 0; j < snapshot.getByIndex(i); j++) {
			// TODO
			// if (i == rowIndex && j == columnIndex)
			// return datatable[i][j];
			// }
		}
		return null;
	}
}
