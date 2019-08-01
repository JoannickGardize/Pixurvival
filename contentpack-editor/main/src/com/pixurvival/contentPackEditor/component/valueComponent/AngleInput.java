package com.pixurvival.contentPackEditor.component.valueComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.pixurvival.core.util.MathUtils;

import lombok.SneakyThrows;

public class AngleInput extends NumberInput<Double> {

	private static final long serialVersionUID = 1L;

	private static final double DEG_TO_RAD = Math.PI / 180;
	private static final double RAD_TO_DEG = 180 / Math.PI;
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

	public AngleInput() {
		super(Bounds.none());
	}

	@Override
	@SneakyThrows
	protected Double parse(String text) {
		if (text.matches("\\-?\\d+(\\.\\d+)?")) {
			return MathUtils.normalizeAngle(DECIMAL_FORMAT.parse(text).doubleValue() * DEG_TO_RAD);
		} else {
			return null;
		}
	}

	@Override
	protected String format(Double value) {
		return DECIMAL_FORMAT.format(value * RAD_TO_DEG);
	}

}
