package com.pixurvival.contentPackEditor.component.valueComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import lombok.SneakyThrows;

public class FloatInput extends NumberInput<Float> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.US));

	@SneakyThrows
	@Override
	protected Float parse(String text) {
		if (text.matches("\\-?\\d+(\\.\\d+)?")) {
			return DECIMAL_FORMAT.parse(text).floatValue();
		}
		return null;
	}

	@Override
	protected String format(Float number) {
		return DECIMAL_FORMAT.format(number);
	}
}
