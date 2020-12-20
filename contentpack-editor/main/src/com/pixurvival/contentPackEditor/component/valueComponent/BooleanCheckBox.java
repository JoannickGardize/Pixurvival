package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.pixurvival.contentPackEditor.component.util.MouseClickWrapper;

public class BooleanCheckBox extends JCheckBox implements ValueComponent<Boolean> {

	private static final long serialVersionUID = 1L;

	private List<ValueChangeListener<Boolean>> listeners = new ArrayList<>();

	public BooleanCheckBox() {
		addActionListener(e -> {
			listeners.forEach(l -> l.valueChanged(isSelected()));
		});
	}

	@Override
	public Boolean getValue() {
		return isSelected();
	}

	@Override
	public void setValue(Boolean value) {
		setSelected(value);
	}

	@Override
	public boolean isValueValid(Boolean value) {
		return true;
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		label.addMouseListener(new MouseClickWrapper(e -> doClick()));
	}

	@Override
	public JLabel getAssociatedLabel() {
		return null;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<Boolean> listener) {
		listeners.add(listener);
	}

}
