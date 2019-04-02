package com.pixurvival.contentPackEditor.component.valueComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import lombok.SneakyThrows;

public class DoubleInput extends NumberInput<Double> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

	public DoubleInput(Bounds valueBounds) {
		super(valueBounds);
	}

	@SneakyThrows
	@Override
	protected Double parse(String text) {
		if (text.matches("\\-?\\d+(\\.\\d+)?")) {
			return DECIMAL_FORMAT.parse(text).doubleValue();
		}
		return null;
	}

	@Override
	protected String format(Double number) {
		return DECIMAL_FORMAT.format(number);
	}
}
