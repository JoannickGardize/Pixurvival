package com.pixurvival.gdxcore.textures;

import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SpriteSheetImageKey {

	private String image;
	private int width;
	private int height;

	public SpriteSheetImageKey(SpriteSheet spriteSheet) {
		image = spriteSheet.getImage();
		width = spriteSheet.getWidth();
		height = spriteSheet.getHeight();
	}
}
