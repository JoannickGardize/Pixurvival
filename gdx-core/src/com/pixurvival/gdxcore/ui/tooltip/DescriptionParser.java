package com.pixurvival.gdxcore.ui.tooltip;

import java.util.HashSet;

import com.pixurvival.core.livingEntity.alteration.StatAmount;
import com.pixurvival.core.livingEntity.alteration.StatMultiplier;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.gdxcore.util.IntWrapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DescriptionParser {

	private static final HashSet<Character> numberSet = new HashSet<>();
	private static final HashSet<Character> endWordSet = new HashSet<>();

	static {
		numberSet.add('-');
		numberSet.add('+');
		numberSet.add('0');
		numberSet.add('1');
		numberSet.add('2');
		numberSet.add('3');
		numberSet.add('4');
		numberSet.add('5');
		numberSet.add('6');
		numberSet.add('7');
		numberSet.add('8');
		numberSet.add('9');
		numberSet.add(' ');
		numberSet.add('.');

		endWordSet.add(' ');
		endWordSet.add('+');
		endWordSet.add('-');
		endWordSet.add('}');
	}

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
		IntWrapper currentCursor = new IntWrapper();
		StatAmount statAmount = new StatAmount();
		while (currentCursor.getValue() < value.length()) {
			Float multiplier = nextNumber(value, currentCursor);
			if (multiplier != null) {
				String statTypeTag = nextWord(value, currentCursor);
				StatType statType = getStatTypeFromTag(statTypeTag);
				if (statType == null) {
					statAmount.setBase(multiplier);
				} else {
					statAmount.getStatMultipliers().add(new StatMultiplier(statType, multiplier));
				}
			}
		}
		description.add(statAmount);
	}

	private static Float nextNumber(String str, IntWrapper currentCursor) {
		StringBuilder sb = new StringBuilder();
		int i = currentCursor.getValue();
		char c;
		boolean numberStarted = false;
		while (i < str.length() && numberSet.contains(c = str.charAt(i)) && (!numberStarted || c != '+' && c != '-')) {
			if (c != ' ') {
				numberStarted = true;
				sb.append(c);
			}
			i++;
		}
		if (sb.length() == 0) {
			return null;
		} else {
			currentCursor.setValue(i);
			return Float.parseFloat(sb.toString());
		}
	}

	private static String nextWord(String str, IntWrapper currentCursor) {
		StringBuilder sb = new StringBuilder();
		int i = currentCursor.getValue();
		char c;
		while (i < str.length() && !endWordSet.contains(c = str.charAt(i))) {
			sb.append(c);
			i++;
		}
		currentCursor.setValue(i);
		return sb.toString();
	}

	private static StatType getStatTypeFromTag(String tag) {
		switch (tag) {
		case "s":
			return StatType.STRENGTH;
		case "a":
			return StatType.AGILITY;
		case "i":
			return StatType.INTELLIGENCE;
		case "mh":
			return StatType.MAX_HEALTH;
		case "sp":
			return StatType.SPEED;
		case "ar":
			return StatType.ARMOR;
		default:
			return null;
		}
	}
}
