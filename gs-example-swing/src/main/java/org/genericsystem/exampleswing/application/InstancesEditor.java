package org.genericsystem.exampleswing.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
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

		Generic[] attributes = new Generic[]{engine.find(Power.class),engine.find(CarColor.class)};

		JTable table = new JTable(tableModel= new InstancesTableModel(attributes),buildTableColumnModel(attributes));

		JScrollPane scrollTable = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		table.addMouseListener(new JTableButtonMouseListener(table));

		getContentPane().add(scrollTable, BorderLayout.CENTER);
		getContentPane().add(new CreatePanel(), BorderLayout.EAST);
		getContentPane().add(new CacheManager(engine, this), BorderLayout.SOUTH);

		getContentPane().setPreferredSize(new Dimension(700, 700));
		pack();
		setVisible(true);
	}

	private TableColumnModel buildTableColumnModel(Generic... attributes){
		TableColumnModel columnModel = new DefaultTableColumnModel();
		int i=0;
		TableColumn tableColumn = new TableColumn(i++);
		tableColumn.setHeaderValue( Objects.toString(type.getValue()));
		columnModel.addColumn(tableColumn);
		for(Generic attribute : attributes){
			tableColumn = new TableColumn(i++,75,getRenderer(attribute),getEditor(attribute));
			columnModel.addColumn(tableColumn);
			tableColumn.setHeaderValue( Objects.toString(attribute.getValue()));
		}
		tableColumn = new TableColumn(i++,75,new JTableButtonRenderer(),null);
		columnModel.addColumn(tableColumn);
		return columnModel;
	}

	private TableCellRenderer getRenderer(Generic attribute){
		if(!isAssociation(attribute))
			return null;
		return new ComboBoxRenderer(new String[]{"Red","Green"});
	}

	private TableCellEditor getEditor(Generic attribute){
		if(!isAssociation(attribute))
			return null;
		return new ComboBoxEditor(new String[]{"Red","Green"});
	}

	private boolean isAssociation(Generic attribute){
		return attribute.getComponents().size()==2;
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

		//		@Override
		//		public String getColumnName(int columnIndex) {
		//			if (columnIndex == 0)
		//				return Objects.toString(type.getValue());
		//			if (columnIndex == getColumnCount() - 1)
		//				return "Delete";
		//			return Objects.toString(attributes[columnIndex - 1].getValue());
		//		}

		@Override
		public int getColumnCount() {
			return attributes.length + 2;
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

						Generic generic = type.getSubInstances().getByIndex(rowIndex);
						int returnCode = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(button), "Are you sure you want to delete generic : " + generic.info());
						if (JOptionPane.OK_OPTION == returnCode) {
							generic.remove();
							fireTableDataChanged();
						}
					}
				});
				return button;
			}
			return Objects.toString(generic.getValue(attributes[columnIndex - 1]));
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Generic generic = type.getSubInstances().getByIndex(rowIndex);
			if (columnIndex == 0) {
				generic.updateValue(Objects.toString(value));
				return;
			}
			assert columnIndex != getColumnCount() - 1;
			generic.setHolder(attributes[columnIndex - 1], Integer.parseInt(Objects.toString(value)));
			fireTableDataChanged();
		}
	}

	public class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else{
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText( (value ==null) ? "" : value.toString() );
			return this;
		}
	}
	
	public class ButtonEditor extends DefaultCellEditor {
		  protected JButton button;
		  private String    label;
		  private boolean   isPushed;
		  
		  public ButtonEditor(JCheckBox checkBox) {
		    super(checkBox);
		    button = new JButton();
		    button.setOpaque(true);
		    button.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        fireEditingStopped();
		      }
		    });
		  }
		  
		  public Component getTableCellEditorComponent(JTable table, Object value,
		                   boolean isSelected, int row, int column) {
		    if (isSelected) {
		      button.setForeground(table.getSelectionForeground());
		      button.setBackground(table.getSelectionBackground());
		    } else{
		      button.setForeground(table.getForeground());
		      button.setBackground(table.getBackground());
		    }
		    label = (value ==null) ? "" : value.toString();
		    button.setText( label );
		    isPushed = true;
		    return button;
		  }
		  
		  public Object getCellEditorValue() {
		    if (isPushed)  {
		      //
		      //
		      JOptionPane.showMessageDialog(button ,label + ": Ouch!");
		      // System.out.println(label + ": Ouch!");
		    }
		    isPushed = false;
		    return new String( label ) ;
		  }
		    
		  public boolean stopCellEditing() {
		    isPushed = false;
		    return super.stopCellEditing();
		  }
		  
		  protected void fireEditingStopped() {
		    super.fireEditingStopped();
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
				button.setBackground(/*table.getBackground()*/UIManager.getColor("Button.background"));
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

	private class ComboBoxRenderer extends JComboBox implements TableCellRenderer {
		public ComboBoxRenderer(String[] items) {
			super(items);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,	boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			setSelectedItem(value);
			return this;
		}
	}

	private class ComboBoxEditor extends DefaultCellEditor {
		public ComboBoxEditor(String[] items) {
			super(new JComboBox(items));
		}
	}
}
