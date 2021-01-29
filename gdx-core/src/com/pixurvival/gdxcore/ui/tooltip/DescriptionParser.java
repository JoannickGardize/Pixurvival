package com.pixurvival.gdxcore.ui.tooltip;

import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DescriptionParser {

	private Description description;
	private int cursor;

	public static Description parse(String descriptionStr) {
		description = new Description();
		int length = descriptionStr.length();
		cursor = 0;
		StringBuilder sb = new StringBuilder();
		while (cursor < length) {
			char c = descriptionStr.charAt(cursor);
			if (c == '{' && cursor < length - 2) {
				if (sb.length() > 0) {
					description.add(sb.toString());
					sb.setLength(0);
				}
				if (descriptionStr.charAt(cursor + 1) == '@') {
					cursor += 2;
					String key = readEndingExpression(descriptionStr, ' ');
					String value = readEndingExpression(descriptionStr, '}');
					if (key != null && value != null) {
						processSpecialExpression(key, value);
					}
				}
			} else {
				sb.append(c);
				cursor++;
			}
		}
		if (sb.length() > 0) {
			description.add(sb.toString());
			sb.setLength(0);
		}
		return description;
	}

	private static String readEndingExpression(String description, char stopChar) {
		StringBuilder sb = new StringBuilder();
		char c;
		while ((c = description.charAt(cursor)) != stopChar && cursor < description.length()) {
			sb.append(c);
			cursor++;
		}
		if (c == stopChar) {
			cursor++;
			return sb.toString();
		} else {
			return null;
		}
	}

	private static void processSpecialExpression(String key, String value) {
		switch (key) {
		case "formula":
			processFormula(value);
			break;
		default:
		}
	}

	private static void processFormula(String value) {
		String trimValue = value.trim();
		if (!trimValue.matches("\\d+")) {
			return;
		}
		StatFormula statFormula = PixurvivalGame.getWorld().getContentPack().getStatFormulas().get(Long.parseLong(trimValue));
		if (statFormula == null) {
			description.add(" ?? ");
		} else {
			description.add(statFormula);
		}
	}

}
