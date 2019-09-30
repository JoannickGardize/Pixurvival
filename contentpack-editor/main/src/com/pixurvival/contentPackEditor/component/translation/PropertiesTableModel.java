package com.pixurvival.contentPackEditor.component.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.pixurvival.contentPackEditor.TranslationService;

public class PropertiesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private Properties properties = new Properties();
	private List<String> orderedKeys = new ArrayList<>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setProperties(Properties properties) {
		this.properties = properties;
		orderedKeys.clear();
		orderedKeys.addAll((Set) properties.keySet());
		orderedKeys.sort((k1, k2) -> k1.compareTo(k2));
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return properties.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return orderedKeys.get(rowIndex);
		} else {
			return properties.getProperty(orderedKeys.get(rowIndex));
		}
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return TranslationService.getInstance().getString("translation.table.key");
		} else {
			return TranslationService.getInstance().getString("translation.table.value");
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			properties.setProperty(orderedKeys.get(rowIndex), (String) aValue);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}
}
