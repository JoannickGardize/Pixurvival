package com.pixurvival.gdxcore.ui.tooltip;

import java.util.Locale;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.alteration.StatMultiplier;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RepresenterUtils {

	public static final float ITEM_WIDTH = 20;

	private static StringBuilder sb = new StringBuilder();

	public static String formatHoursMinutesSecondes(long time) {
		return formatHoursMinutesSecondes(time, false);
	}

	public static String formatHoursMinutesSecondes(long time, boolean optionalHours) {
		long hours = time / 3_600_000;
		long minutes = (time - hours * 3_600_000) / 60_000;
		long sec = (time - hours * 3_600_000 - minutes * 60_000) / 1000;
		sb.setLength(0);
		if (!optionalHours || hours > 0) {
			sb.append(twoDigits(hours)).append(":");
		}
		return sb.append(twoDigits(minutes)).append(":").append(twoDigits(sec)).toString();

	}

	public static String twoDigits(long value) {
		if (value < 10) {
			return "0" + value;
		} else {
			return String.valueOf(value);
		}
	}

	public static void appendTime(StringBuilder sb, long time) {
		long m = time / 60_000;
		float s = (time - m) / 1000f;
		if (m != 0) {
			sb.append(m).append("m");
		}
		if (s != 0) {
			if (m != 0) {
				sb.append(" ");
			}
			sb.append(PixurvivalGame.DECIMAL_FORMAT.format(s)).append("s");
		}
	}

	public static String time(long time) {
		sb.setLength(0);
		appendTime(sb, time);
		return sb.toString();
	}

	public static String getColorTag(StatType statType) {
		switch (statType) {
		case STRENGTH:
		case MAX_HEALTH:
		case ARMOR:
			return "[strength]";
		case AGILITY:
		case SPEED:
			return "[agility]";
		case INTELLIGENCE:
			return "[intelligence]";
		default:
			throw new IllegalArgumentException();
		}
	}

	public static String getTranslation(StatType statType) {
		return PixurvivalGame.getString("statType." + CaseUtils.upperToCamelCase(statType.name()));
	}

	public static void appendStatAmount(StringBuilder sb, StatFormula statAmount) {
		sb.append(PixurvivalGame.DECIMAL_FORMAT.format(statAmount.getBase() + statAmount.getRandomValue() / 2f));
		if (!statAmount.getStatMultipliers().isEmpty()) {
			sb.append(" ");
		}
		StatSet stats = PixurvivalGame.getClient().getMyPlayer().getStats();
		for (StatMultiplier statMultiplier : statAmount.getStatMultipliers()) {
			StatType type = statMultiplier.getStatType();
			sb.append(getColorTag(type)).append("(+").append(PixurvivalGame.DECIMAL_FORMAT.format(stats.getValue(type) * statMultiplier.getMultiplier())).append(")");
		}
		if (statAmount.getRandomValue() != 0) {
			sb.append("[WHITE](+/-").append(PixurvivalGame.DECIMAL_FORMAT.format(statAmount.getRandomValue() / 2f)).append(")");
		}
	}

	public static Table createTable() {
		Table table = new Table();
		table.defaults().pad(2).fill();
		return table;
	}

	public static String statAmount(StatFormula statAmount) {
		sb.setLength(0);
		RepresenterUtils.appendStatAmount(sb, statAmount);
		return sb.toString();
	}

	public static void appendLabelledRow(Table table, String labelKey, StatFormula statAmount) {
		appendLabelledRow(table, labelKey, statAmount(statAmount));
	}

	public static void appendLabelledRow(Table table, String labelKey, String value) {
		appendRawLabelledRow(table, PixurvivalGame.getString(labelKey), value);
	}

	public static void appendRawLabelledRow(Table table, String label, String value) {
		sb.setLength(0);
		sb.append(label).append(" ");
		table.add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).expand();
		Label valueLabel = new Label(value, PixurvivalGame.getSkin(), "white");
		valueLabel.setAlignment(Align.right);
		table.add(valueLabel).fill();
		table.row();
	}

	public static void appendItemStack(Table table, ItemStack itemStack) {
		ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		Texture itemTexture = PixurvivalGame.getContentPackTextures().getItem(itemStack.getItem().getId()).getTexture();
		table.add(new Image(itemTexture)).size(ITEM_WIDTH, ITEM_WIDTH);
		sb.setLength(0);
		sb.append(contentPack.getTranslation(locale, itemStack.getItem(), TranslationKey.NAME));
		sb.append(" x ");
		sb.append(itemStack.getQuantity());
		Label nameAndQuantityLabel = new Label(sb.toString(), PixurvivalGame.getSkin(), "white");
		table.add(nameAndQuantityLabel);
	}

	public static Table itemStack(ItemStack itemStack) {
		Table table = new Table();
		appendItemStack(table, itemStack);
		return table;
	}

	public static Table labelledStatAmount(String labelKey, StatFormula statAmount) {
		Table table = createTable();
		appendLabelledRow(table, labelKey, statAmount);
		return table;
	}

	public static Table labelledValue(String labelKey, float value) {
		Table table = createTable();
		appendLabelledRow(table, labelKey, PixurvivalGame.DECIMAL_FORMAT.format(value));
		return table;
	}

	public static Table labelledValue(String labelKey, Object value) {
		Table table = createTable();
		appendLabelledRow(table, labelKey, value.toString());
		return table;
	}

	public static String statValue(StatType type, float value) {
		if (type == StatType.ARMOR) {
			return PixurvivalGame.DECIMAL_FORMAT.format(value * 100) + "%";
		} else {
			return PixurvivalGame.DECIMAL_FORMAT.format(value);
		}
	}
}
