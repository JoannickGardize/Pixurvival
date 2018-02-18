package com.pixurvival.gdxcore.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.pixurvival.core.contentPack.ContentPackReadException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SpriteSheetPixmap extends Pixmap {

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public class Region {

		private int xIndex;
		private int yIndex;

		public int getPixel(int x, int y) {
			return SpriteSheetPixmap.this.getPixel(xIndex * getSpriteWidth() + x, yIndex * getSpriteHeight() + y);
		}

		public Format getFormat() {
			return SpriteSheetPixmap.this.getFormat();
		}

		public int getWidth() {
			return spriteWidth;
		}

		public int getHeight() {
			return spriteHeight;
		}
	}

	private int spriteWidth;
	private int spriteHeight;

	public SpriteSheetPixmap(byte[] data, int spriteWidth, int spriteHeight) throws ContentPackReadException {
		super(data, 0, data.length);
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		if (getWidth() % spriteWidth != 0 || getHeight() % spriteHeight != 0) {
			throw new ContentPackReadException("Illegal width/height for sprite sheet.");
		}
	}

	public int getTileCountX() {
		return getWidth() / spriteWidth;
	}

	public int getTileCountY() {
		return getHeight() / spriteHeight;
	}

	public Region getRegion(int xIndex, int yIndex) {
		return new Region(xIndex, yIndex);
	}

}
