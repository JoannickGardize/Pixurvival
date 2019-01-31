package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.pixurvival.contentPackEditor.component.util.DocumentAdapter;

import lombok.Getter;

public abstract class FormattedTextInput<T> extends JTextField implements ValueComponent<T> {

	private static final long serialVersionUID = 1L;

	private @Getter JLabel associatedLabel;
	private @Getter T value;
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();
	private boolean settingValue = false;;

	public FormattedTextInput() {
		super(2);
		getDocument().addDocumentListener(new DocumentAdapter(e -> updateValue()));
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (value != null) {
					setText(format(value));
					if (isValueValid()) {
						setForeground(Color.BLACK);
					} else {
						setForeground(Color.RED);
					}
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
	public boolean isValueValid(T value) {
		return value != null;
	}

	@Override
	public void setForeground(Color fg) {
		if (associatedLabel != null) {
			associatedLabel.setForeground(fg);
		}
		super.setForeground(fg);
	}

	@Override
	public void setValue(T value) {
		settingValue = true;
		if (value == null) {
			setText("");
		} else {
			setText(format(value));
		}
		setForeground(isValueValid(value) ? Color.BLACK : Color.RED);
		this.value = value;
		settingValue = false;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<T> listener) {
		listeners.add(listener);
	}

	protected abstract T parse(String text);

	protected abstract String format(T number);

	private void updateValue() {
		if (settingValue) {
			// Do not update and notify when the value change is programmatic
			return;
		}
		String text = getText().trim();
		T newValue = parse(text);
		if (isValueValid(newValue)) {
			setForeground(Color.BLACK);
			if (!Objects.equals(newValue, value)) {
				value = newValue;
				listeners.forEach(l -> l.valueChanged(value));
			}
		} else {
			setForeground(Color.RED);
		}
	}

}
