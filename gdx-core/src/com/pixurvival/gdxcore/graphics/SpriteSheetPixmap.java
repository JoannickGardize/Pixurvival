package com.pixurvival.gdxcore.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

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

	public SpriteSheetPixmap(FileHandle file, int spriteWidth, int spriteHeight) {
		super(file);
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
	}

	public Region getRegion(int xIndex, int yIndex) {
		return new Region(xIndex, yIndex);
	}

}
