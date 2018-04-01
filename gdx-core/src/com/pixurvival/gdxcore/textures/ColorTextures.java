package com.pixurvival.gdxcore.textures;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class ColorTextures {

	private static Map<Color, Texture> textures = new HashMap<>();

	public static Texture get(Color color) {
		Texture texture = textures.get(color);
		if (texture == null) {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.drawPixel(0, 0, Color.rgba8888(color));
			texture = new Texture(pixmap);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textures.put(color, texture);
		}
		return texture;
	}
}
