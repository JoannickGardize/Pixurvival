package com.pixurvival.contentPackEditor.component.util;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;

import lombok.Getter;

public class IntegerInput extends JTextField implements ValueComponent<Integer> {

	private static final long serialVersionUID = 1L;

	private @Getter JLabel associatedLabel;
	private @Getter Integer value;
	private List<ValueChangeListener<Integer>> listeners = new ArrayList<>();

	public IntegerInput() {
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
		return value != null;
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
		if (text.matches("\\d+")) {
			value = Integer.valueOf(text);
			setForeground(Color.BLACK);
			listeners.forEach(l -> l.valueChanged(value));
		} else {
			setForeground(Color.RED);
		}
	}

	@Override
	public void setValue(Integer value) {
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
	public void addValueChangeListener(ValueChangeListener<Integer> listener) {
		listeners.add(listener);
	}
}
