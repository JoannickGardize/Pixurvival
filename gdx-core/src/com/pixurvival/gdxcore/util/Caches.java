package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.pixurvival.gdxcore.PixurvivalGame;

public class Caches {

	public static final Cache<String, GlyphLayout> whiteGlyphLayout = new Cache<>(s -> {
		BitmapFont font = PixurvivalGame.getSkin().getFont("default");
		font.setColor(Color.WHITE);
		return new GlyphLayout(font, s);
	});

	public static final Cache<String, GlyphLayout> redGlyphLayout = new Cache<>(s -> {
		BitmapFont font = PixurvivalGame.getSkin().getFont("default");
		font.setColor(Color.RED);
		return new GlyphLayout(font, s);
	});
}
