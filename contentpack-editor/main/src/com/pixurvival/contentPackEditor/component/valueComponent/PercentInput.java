package com.pixurvival.contentPackEditor.component.valueComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import lombok.SneakyThrows;

public class PercentInput extends NumberInput<Float> {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.US));

	public PercentInput() {
		super(Bounds.none());
	}

	public PercentInput(Bounds bounds) {
		super(bounds);
	}

	@Override
	@SneakyThrows
	protected Float parse(String text) {
		String trimmedText = text.trim();
		if (trimmedText.matches("\\-?\\d+(\\.\\d+)?%")) {
			return DECIMAL_FORMAT.parse(trimmedText.substring(0, trimmedText.length() - 1)).floatValue() / 100f;
		} else {
			return null;
		}
	}

	@Override
	protected String format(Float value) {
		return DECIMAL_FORMAT.format(value * 100f) + "%";
	}

}
