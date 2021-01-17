package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UIConstants {

	public static final Color STRENGTH_COLOR = new Color(0xE22B2BFF);
	public static final Color AGILITY_COLOR = new Color(0x25A834FF);
	public static final Color INTELLIGENCE_COLOR = new Color(0x58ABD8FF);

	public static final Color SEPARATOR_COLOR = new Color(0.33f, 0.33f, 0.33f, 1f);

	static {
		Colors.put("strength", STRENGTH_COLOR);
		Colors.put("agility", AGILITY_COLOR);
		Colors.put("intelligence", INTELLIGENCE_COLOR);

	}
}
