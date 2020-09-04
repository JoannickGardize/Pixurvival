package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Color;

import com.pixurvival.contentPackEditor.util.ColorUtils;

public class ColorInput extends FormattedTextInput<Integer> {

	private static final long serialVersionUID = 1L;

	public ColorInput() {
		super(7);
	}

	@Override
	protected Integer parse(String text) {
		if (text.length() != 7 || !text.startsWith("#")) {
			return null;
		}
		try {
			return Integer.parseInt(text.substring(1, 7), 16);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected String format(Integer value) {
		return String.format("#%06x", value & 0x00FFFFFF);
	}

	@Override
	protected void onValueChanged() {
		setBackground(new Color(getValue()));
	}

	@Override
	protected void onInvalidInput() {
		setBackground(Color.WHITE);
	}

	@Override
	public Color getValidForeground() {
		if (ColorUtils.getLuminance(new Color(getValue())) < 0.5) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}
}
