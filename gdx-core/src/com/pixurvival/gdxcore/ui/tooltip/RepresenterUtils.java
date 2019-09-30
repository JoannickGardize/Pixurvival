package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.livingEntity.alteration.StatAmount;
import com.pixurvival.core.livingEntity.alteration.StatMultiplier;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.util.CaseUtils;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RepresenterUtils {

	private static StringBuilder sb = new StringBuilder();

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
			sb.append(s).append("s");
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

	public static void appendStatAmount(StringBuilder sb, StatAmount statAmount) {
		sb.append(statAmount.getBase()).append(" ");
		StatSet stats = PixurvivalGame.getClient().getMyPlayer().getStats();
		for (StatMultiplier statMultiplier : statAmount.getStatMultipliers()) {
			StatType type = statMultiplier.getStatType();
			sb.append(getColorTag(type)).append("(+").append(stats.getValue(type) * statMultiplier.getMultiplier()).append(") ");
		}
	}

	public static Table createTable() {
		Table table = new Table();
		table.defaults().pad(2).fill();
		return table;
	}

	public static void appendLabelledRow(Table table, String labelKey, StatAmount statAmount) {
		sb.setLength(0);
		RepresenterUtils.appendStatAmount(sb, statAmount);
		appendLabelledRow(table, labelKey, sb.toString());
	}

	public static void appendLabelledRow(Table table, String labelKey, String value) {
		sb.setLength(0);
		sb.append(PixurvivalGame.getString(labelKey)).append(" ");
		table.add(new Label(sb.toString(), PixurvivalGame.getSkin(), "white")).expand();
		Label valueLabel = new Label(value, PixurvivalGame.getSkin(), "white");
		valueLabel.setAlignment(Align.right);
		table.add(valueLabel);
		table.row();
	}

	public static Table labelledStatAmount(String labelKey, StatAmount statAmount) {
		Table table = createTable();
		appendLabelledRow(table, labelKey, statAmount);
		return table;
	}

	public static Table labelledValue(String labelKey, Object value) {
		Table table = createTable();
		appendLabelledRow(table, labelKey, value.toString());
		return table;
	}
}
