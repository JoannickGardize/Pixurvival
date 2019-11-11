package com.pixurvival.contentPackEditor.component.translation;

import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JTable;

public class PropertiesTable extends JTable {

	private static final long serialVersionUID = 1L;

	public PropertiesTable() {
		super(new PropertiesTableModel());
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		PropertiesTable table = new PropertiesTable();
		PropertiesTableModel model = (PropertiesTableModel) table.getModel();
		Properties prop = new Properties();
		model.setProperties(prop);
		frame.setContentPane(table);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
