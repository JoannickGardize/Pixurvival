package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.pixurvival.contentPackEditor.component.util.DocumentAdapter;

import lombok.Getter;
import lombok.Setter;

public abstract class NumberInput<T extends Number> extends JTextField implements ValueComponent<T> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

	private @Getter JLabel associatedLabel;
	private @Getter T value;
	private @Getter @Setter Bounds valueBounds = Bounds.noBounds();
	private List<ValueChangeListener<T>> listeners = new ArrayList<>();

	private NumberInput(Bounds valueBounds) {
		super(2);
		this.valueBounds = valueBounds;
		getDocument().addDocumentListener(new DocumentAdapter(e -> updateValue()));
		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (value != null) {
					setText(DECIMAL_FORMAT.format(value));
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

	public static NumberInput<Integer> integerInput() {
		return integerInput(Bounds.noBounds());
	}

	public static NumberInput<Integer> integerInput(Bounds valueBounds) {
		return new NumberInput<Integer>(valueBounds) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Integer parse(String text) {
				if (text.matches("\\d+")) {
					return Integer.valueOf(text);
				} else {
					return null;
				}
			}
		};
	}

	public static NumberInput<Double> doubleInput() {
		return doubleInput(Bounds.noBounds());
	}

	public static NumberInput<Double> doubleInput(Bounds valueBounds) {
		return new NumberInput<Double>(valueBounds) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Double parse(String text) {
				try {
					return DECIMAL_FORMAT.parse(text).doubleValue();
				} catch (ParseException e) {
					return null;
				}
			}
		};
	}

	@Override
	public void setAssociatedLabel(JLabel label) {
		associatedLabel = label;
		associatedLabel.setForeground(getForeground());
	}

	@Override
	public boolean isValueValid(T value) {
		return value != null && valueBounds.test(value);
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
		if (value == null) {
			setText("");
			setForeground(Color.RED);
		} else {
			setText(DECIMAL_FORMAT.format(value));
			setForeground(Color.BLACK);
		}
		this.value = value;
	}

	@Override
	public void addValueChangeListener(ValueChangeListener<T> listener) {
		listeners.add(listener);
	}

	protected abstract T parse(String text);

	private void updateValue() {
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
