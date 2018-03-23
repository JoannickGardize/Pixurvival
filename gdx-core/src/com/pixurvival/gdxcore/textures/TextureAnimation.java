package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.Frame;

import lombok.Getter;
import lombok.Setter;

public class TextureAnimation {

	private Texture[] textures;
	@Getter
	private double frameDuration;
	private @Getter int shadowWidth;
	private @Getter float worldShadowWidth;
	private @Getter @Setter Texture shadow;

	public TextureAnimation(TextureSheet textureSheet, Animation animation, double frameDuration) {
		this.frameDuration = frameDuration;
		Frame[] frames = animation.getFrames();
		textures = new Texture[frames.length];
		for (int i = 0; i < frames.length; i++) {
			textures[i] = textureSheet.get(frames[i].getX(), frames[i].getY());
		}
		TextureMetrics metrics = textureSheet.getMetrics(frames[0].getX(), frames[0].getY());
		shadowWidth = metrics.getWidth();
		worldShadowWidth = metrics.getWorldWidth();
	}

	public Texture getTexture(int index) {
		return textures[index];
	}

	public int size() {
		return textures.length;
	}
}
