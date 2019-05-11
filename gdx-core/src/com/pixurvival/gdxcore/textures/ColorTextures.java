package com.pixurvival.gdxcore.textures;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ColorTextures {

	private static Map<Color, Texture> textures = new HashMap<>();

	public static Texture get(Color color) {
		return textures.computeIfAbsent(color, c -> {
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.drawPixel(0, 0, Color.rgba8888(c));
			Texture texture = new Texture(pixmap);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textures.put(c, texture);
			return texture;
		});
	}
}
