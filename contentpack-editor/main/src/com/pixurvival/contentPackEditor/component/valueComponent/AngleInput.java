package com.pixurvival.contentPackEditor.component.valueComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.pixurvival.core.util.MathUtils;

import lombok.SneakyThrows;

public class AngleInput extends NumberInput<Float> {

	private static final long serialVersionUID = 1L;

	private static final float DEG_TO_RAD = (float) Math.PI / 180;
	private static final float RAD_TO_DEG = 180 / (float) Math.PI;
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

	public AngleInput() {
		super(Bounds.none());
	}

	@Override
	@SneakyThrows
	protected Float parse(String text) {
		if (text.matches("\\-?\\d+(\\.\\d+)?")) {
			// TODO normalize or not ? angle per secondes could requires more
			return MathUtils.normalizeAngle(DECIMAL_FORMAT.parse(text).floatValue() * DEG_TO_RAD);
		} else {
			return null;
		}
	}

	@Override
	protected String format(Float value) {
		return DECIMAL_FORMAT.format(value * RAD_TO_DEG);
	}

}
