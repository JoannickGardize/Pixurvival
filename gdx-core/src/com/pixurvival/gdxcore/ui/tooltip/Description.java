package com.pixurvival.gdxcore.ui.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.alteration.StatFormula;

public class Description {

	private List<Consumer<StringBuilder>> elements = new ArrayList<>();

	public void add(String str) {
		elements.add(sb -> sb.append(str));
	}

	public void add(StatFormula statAmount) {
		elements.add(sb -> {
			RepresenterUtils.appendStatAmount(sb, statAmount);
			sb.append("[WHITE]");
		});
	}

	public String get() {
		StringBuilder sb = new StringBuilder();
		for (Consumer<StringBuilder> element : elements) {
			element.accept(sb);
		}
		return sb.toString();
	}
}
