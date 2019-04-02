package com.pixurvival.contentPackEditor.component.valueComponent;

public class IntegerInput extends NumberInput<Integer> {

	private static final long serialVersionUID = 1L;

	public IntegerInput(Bounds bounds) {
		super(bounds);
	}

	@Override
	protected Integer parse(String text) {
		if (text.matches("\\-?\\d+")) {
			return Integer.valueOf(text);
		} else {
			return null;
		}
	}

	@Override
	protected String format(Integer number) {
		return number.toString();
	}
}
