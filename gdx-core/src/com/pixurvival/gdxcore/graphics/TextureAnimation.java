package com.pixurvival.gdxcore.graphics;

import com.badlogic.gdx.graphics.Texture;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class TextureAnimation {

	private Texture[] textures;
	@Getter
	private float frameDuration;

	public Texture getTexture(int index) {
		return textures[index];
	}

	public int size() {
		return textures.length;
	}
}
