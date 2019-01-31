package com.pixurvival.contentPackEditor.component.valueComponent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StringInput extends FormattedTextInput<String> {

	private static final long serialVersionUID = 1L;

	private String regex;

	public StringInput(int minLength) {
		regex = ".{" + minLength + ",}";
	}

	@Override
	protected String parse(String text) {
		String result = text.trim();
		if (result.matches(regex)) {
			return result;
		} else {
			return null;
		}
	}

	@Override
	protected String format(String value) {
		return value.trim();
	}

}
