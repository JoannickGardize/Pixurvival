package com.pixurvival.gdxcore.textures;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.sprite.Animation;
import com.pixurvival.core.contentPack.sprite.EquipmentOffset;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.contentPack.sprite.FrameOffset;

import lombok.Getter;
import lombok.Setter;

public class TextureAnimation {

	private Texture[] textures;
	private TextureRegion[] textureRegions;
	private float[] offsetX;
	private float[] offsetY;
	private boolean[] back;
	@Getter
	private float frameDuration;
	private @Getter float rotationPerSecond;
	private @Getter int shadowWidth;
	private @Getter float worldShadowWidth;
	private @Getter @Setter Texture shadow;

	public TextureAnimation(TextureSheet textureSheet, Animation animation, EquipmentOffset equipmentOffset) {
		frameDuration = (float) animation.getFrameDuration() / 1000;
		rotationPerSecond = animation.getRotationPerSecond();
		List<Frame> frames = animation.getFrames();
		textures = new Texture[frames.size()];
		textureRegions = new TextureRegion[frames.size()];
		for (int i = 0; i < frames.size(); i++) {
			Texture texture = textureSheet.get(frames.get(i).getX(), frames.get(i).getY());
			textures[i] = texture;
			textureRegions[i] = new TextureRegion(texture);
		}
		if (equipmentOffset != null) {
			offsetX = new float[frames.size()];
			offsetY = new float[frames.size()];
			back = new boolean[frames.size()];

			for (int i = 0; i < frames.size(); i++) {
				FrameOffset frameOffset = findFrameOffset(equipmentOffset.getFrameOffsets(), frames.get(i));
				offsetX[i] = (float) frameOffset.getOffsetX() / GameConstants.PIXEL_PER_UNIT;
				offsetY[i] = (float) frameOffset.getOffsetY() / GameConstants.PIXEL_PER_UNIT;
				back[i] = frameOffset.isBack();
			}

		}
		TextureMetrics metrics = textureSheet.getMetrics(frames.get(0).getX(), frames.get(0).getY());
		shadowWidth = metrics.getWidth();
		worldShadowWidth = metrics.getWorldWidth();
	}

	public Texture getTexture(int index) {
		return textures[index];
	}

	public TextureRegion getTextureRegion(int index) {
		return textureRegions[index];
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

	public void dispose() {
		for (Texture texture : textures) {
			texture.dispose();
		}
		// do not dispose the shadow texture here, the ContentPackTextures is
		// responsible of it.
	}

	private FrameOffset findFrameOffset(List<FrameOffset> frameOffsets, Frame frame) {
		// TODO put this in a transient map
		for (int i = 0; i < frameOffsets.size(); i++) {
			FrameOffset frameOffset = frameOffsets.get(i);
			if (frame.getX() == frameOffset.getX() && frame.getY() == frameOffset.getY()) {
				return frameOffset;
			}
		}
		throw new IllegalStateException("No frame offset found for " + frame);
	}
}
