package com.pixurvival.gdxcore.graphics;

import java.util.function.Function;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.gdxcore.graphics.SpriteSheetPixmap.Region;

import lombok.Getter;

public class TextureSheet {

	private Texture[] textures;
	private @Getter int sizeX;
	private @Getter int sizeY;

	public TextureSheet(SpriteSheetPixmap spriteSheetPixmap, Function<Region, Pixmap> transform) {
		sizeX = spriteSheetPixmap.getTileCountX();
		sizeY = spriteSheetPixmap.getTileCountY();
		textures = new Texture[sizeX * sizeY];
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				textures[x + y * sizeX] = new Texture(transform.apply(spriteSheetPixmap.getRegion(x, y)));
			}
		}
	}

	public Texture get(int x, int y) {
		return textures[x + y * sizeX];
	}

}
