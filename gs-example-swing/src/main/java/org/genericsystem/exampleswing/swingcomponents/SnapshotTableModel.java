package org.genericsystem.exampleswing.swingcomponents;

import java.io.Serializable;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.mutability.Generic;

public class SnapshotTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -8137410628615273305L;

	protected Snapshot<Generic> snapshot;
	protected List<Serializable> columnsIdentifiers;

	public SnapshotTableModel(Snapshot<Generic> datatable, List<Serializable> columnsIdentifiers) {
		this.snapshot = datatable;
		this.columnsIdentifiers = columnsIdentifiers;
	}

	@Override
	public int getRowCount() {
		return snapshot.size();
	}

	@Override
	public String getColumnName(int column) {
		return (String) columnsIdentifiers.get(column);
	}

	@Override
	public int getColumnCount() {
		return columnsIdentifiers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Generic generic = snapshot.getByIndex(rowIndex);

		switch (columnIndex) {
		case 0:
			return generic != null ? generic.getValue() : null;
		case 1:
			return generic != null ? generic.getHolders(generic.getRoot().find(Power.class)).first().getValue() : null;
		default:
			break;
		}
		return null;
	}
}
