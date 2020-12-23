package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;
import java.awt.Dimension;
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
	private boolean settingValue = false;

	public FormattedTextInput() {
		this(10);
	}

	public FormattedTextInput(int columns) {
		super(columns);
		getDocument().addDocumentListener(new DocumentAdapter(e -> updateValue()));

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (value != null) {
					settingValue = true;
					setText(format(value));
					settingValue = false;
					if (isValueValid()) {
						setForeground(getValidForeground());
						onValueChanged();
					} else {
						setForeground(Color.RED);
						onInvalidInput();
					}
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		setForeground(Color.RED);
		setMinimumSize(new Dimension(50, 10));
	}

	public Color getValidForeground() {
		return Color.BLACK;
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
		this.value = value;
		setForeground(isValueValid(value) ? getValidForeground() : Color.RED);
		settingValue = false;
		onValueChanged();
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * @param text
	 * @return the value represented by text, or null if the text is invalid format.
	 */
	protected abstract T parse(String text);

	protected abstract String format(T value);

	public void notifyValueChanged() {
		listeners.forEach(l -> l.valueChanged(value));
	}

	public void updateValue() {
		if (settingValue) {
			// Do not update and notify when the value change is programmatic
			return;
		}
		String text = getText().trim();
		T newValue = parse(text);
		if (isValueValid(newValue)) {
			if (!Objects.equals(newValue, value)) {
				value = newValue;
				notifyValueChanged();
				onValueChanged();
			}
			setForeground(getValidForeground());
		} else {
			setForeground(Color.RED);
			onInvalidInput();
		}
	}

	protected void onValueChanged() {
		// For override
	}

	protected void onInvalidInput() {
		// For override
	}
}
