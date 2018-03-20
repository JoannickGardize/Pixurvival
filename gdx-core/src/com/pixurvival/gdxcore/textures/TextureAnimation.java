package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;

import lombok.Getter;

public class TextureAnimation {

	private Texture[] textures;
	@Getter
	private double frameDuration;

	public TextureAnimation(TextureSheet textureSheet, Animation animation, double frameDuration) {
		this.frameDuration = frameDuration;
		Frame[] frames = animation.getFrames();
		textures = new Texture[frames.length];
		for (int i = 0; i < frames.length; i++) {
			textures[i] = textureSheet.get(frames[i].getX(), frames[i].getY());
		}

	}

	public Texture getTexture(int index) {
		return textures[index];
	}

	public int size() {
		return textures.length;
	}
}
