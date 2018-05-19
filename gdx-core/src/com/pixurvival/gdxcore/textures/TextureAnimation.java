package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.FrameOffset;

import lombok.Getter;
import lombok.Setter;

public class TextureAnimation {

	private Texture[] textures;
	private float[] offsetX;
	private float[] offsetY;
	private boolean[] back;
	@Getter
	private double frameDuration;
	private @Getter int shadowWidth;
	private @Getter float worldShadowWidth;
	private @Getter @Setter Texture shadow;

	public TextureAnimation(TextureSheet textureSheet, Animation animation, double frameDuration,
			EquipmentOffset equipmentOffset) {
		this.frameDuration = frameDuration;
		Frame[] frames = animation.getFrames();
		textures = new Texture[frames.length];
		for (int i = 0; i < frames.length; i++) {
			textures[i] = textureSheet.get(frames[i].getX(), frames[i].getY());
		}
		if (equipmentOffset != null) {
			offsetX = new float[frames.length];
			offsetY = new float[frames.length];
			back = new boolean[frames.length];

			for (int i = 0; i < frames.length; i++) {
				FrameOffset frameOffset = findFrameOffset(equipmentOffset.getFrameOffsets(), frames[i]);
				offsetX[i] = (float) frameOffset.getOffsetX() / GameConstants.PIXEL_PER_UNIT;
				offsetY[i] = (float) frameOffset.getOffsetY() / GameConstants.PIXEL_PER_UNIT;
				back[i] = frameOffset.isBack();
			}

		}
		TextureMetrics metrics = textureSheet.getMetrics(frames[0].getX(), frames[0].getY());
		shadowWidth = metrics.getWidth();
		worldShadowWidth = metrics.getWorldWidth();
	}

	public Texture getTexture(int index) {
		return textures[index];
	}

	public float getOffsetX(int index) {
		return offsetX[index];
	}

	public float getOffsetY(int index) {
		return offsetY[index];
	}

	public boolean isBack(int index) {
		return back[index];
	}

	public int size() {
		return textures.length;
	}

	private FrameOffset findFrameOffset(FrameOffset[] frameOffsets, Frame frame) {
		for (int i = 0; i < frameOffsets.length; i++) {
			FrameOffset frameOffset = frameOffsets[i];
			if (frame.getX() == frameOffset.getX() && frame.getY() == frameOffset.getY()) {
				return frameOffset;
			}
		}
		throw new IllegalStateException("No frame offset found for " + frame);
	}
}
