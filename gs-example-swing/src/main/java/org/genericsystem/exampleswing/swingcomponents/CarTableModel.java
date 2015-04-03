package org.genericsystem.exampleswing.swingcomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import org.genericsystem.exampleswing.model.Car;
import org.genericsystem.exampleswing.model.Power;
import org.genericsystem.mutability.Generic;

public class CarTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -8137410628615273305L;

	private CarViewer carViewer;
	private Generic car;
	private List<String> columnsNames = new ArrayList<>();

	public CarTableModel(CarViewer carViewer) {
		this.carViewer = carViewer;
		car = carViewer.getEngine().find(Car.class);
		columnsNames.add("Car");
		columnsNames.add("Power");
	}

	@Override
	public int getRowCount() {
		return car.getSubInstances().size();
	}

	@Override
	public String getColumnName(int column) {
		return columnsNames.get(column);
	}

	@Override
	public int getColumnCount() {
		return columnsNames.size();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Generic generic = car.getSubInstances().getByIndex(rowIndex);
		if (generic != null)
			return (columnIndex == 0) ? generic.getValue() : generic.getHolders(generic.getRoot().find(Power.class)).first().getValue();
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Generic generic = car.getSubInstances().getByIndex(rowIndex);
		if (columnIndex == 0)
			generic.updateValue(Objects.toString(aValue));
		else
			generic.setHolder(generic.getRoot().find(Power.class), Integer.parseInt(Objects.toString(aValue)));
		carViewer.refresh();
	}
}
