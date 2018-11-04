package com.pixurvival.contentPackEditor.component.util;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;

import lombok.Getter;
import lombok.Setter;

public class DoubleInput extends JTextField implements ValueComponent<Double> {

	private static final long serialVersionUID = 1L;

	private @Getter JLabel associatedLabel;
	private @Getter Double value;
	private @Getter @Setter Bounds valueBounds;
	private List<ValueChangeListener<Double>> listeners = new ArrayList<>();

	public DoubleInput() {
		super(10);
		getDocument().addDocumentListener(new DocumentAdapter(e -> updateValue()));
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (value != null) {
					setText(value.toString());
					setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		setForeground(Color.RED);
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		associatedLabel = label;
		associatedLabel.setForeground(getForeground());
	}

	@Override
	public boolean isValueValid() {
		return value != null && valueBounds.test(value);
	}

	@Override
	public void setForeground(Color fg) {
		if (associatedLabel != null) {
			associatedLabel.setForeground(fg);
		}
		super.setForeground(fg);
	}

	private void updateValue() {
		String text = getText().trim();
		double tmpValue;
		if (text.matches("\\d*\\.?\\d+") && valueBounds.test(tmpValue = Double.valueOf(text))) {
			value = tmpValue;
			setForeground(Color.BLACK);
			listeners.forEach(l -> l.valueChanged(value));
		} else {
			setForeground(Color.RED);
		}
	}

	@Override
	public void setValue(Double value) {
		if (value == null) {
			setText("");
			setForeground(Color.RED);
		} else {
			setText(value.toString());
			setForeground(Color.BLACK);
		}
		this.value = value;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<Double> listener) {
		listeners.add(listener);
	}
}
