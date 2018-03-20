package com.pixurvival.gdxcore.textures;

import com.pixurvival.core.GameConstants;

import lombok.Data;

@Data
public class TextureMetrics {
	private int offsetX;
	private int offsetY;
	private int width;
	private int height;

	private float worldOffsetX;
	private float worldOffsetY;
	private float worldWidth;
	private float worldHeight;

	public void computeWorldUnits() {
		worldOffsetX = ((float) offsetX) / GameConstants.PIXEL_PER_UNIT;
		worldOffsetY = ((float) offsetY) / GameConstants.PIXEL_PER_UNIT;
		worldWidth = ((float) width) / GameConstants.PIXEL_PER_UNIT;
		worldHeight = ((float) height) / GameConstants.PIXEL_PER_UNIT;
	}
}
