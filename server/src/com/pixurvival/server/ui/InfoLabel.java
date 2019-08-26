package com.pixurvival.server.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class InfoLabel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel valueLabel;

	public InfoLabel(String label, String initialValue) {
		JLabel labelComp = new JLabel(label);
		labelComp.setHorizontalAlignment(SwingConstants.CENTER);
		valueLabel = new JLabel(initialValue);
		setLayout(new BorderLayout());
		add(labelComp, BorderLayout.WEST);
		add(valueLabel, BorderLayout.CENTER);
	}

	public void setInfoValue(String value) {
		valueLabel.setText(value);
		valueLabel.revalidate();
	}
}