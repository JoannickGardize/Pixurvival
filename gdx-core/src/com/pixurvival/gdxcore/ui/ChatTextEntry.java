package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatTextEntry {

	private @NonNull String text;

	private GlyphLayout glyphLayout;

	public void buildGlyphLayout(BitmapFont font) {
		if (glyphLayout == null) {
			glyphLayout = new GlyphLayout(font, text, 0, text.length(), Color.BLACK, 0, Align.left, false, null);
		}
	}

	@Override
	public String toString() {
		return text;
	}
}
