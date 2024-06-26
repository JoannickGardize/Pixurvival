package com.pixurvival.gdxcore.textures;

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

        public void drawAllTo(Pixmap pixmap, int x, int y) {
            pixmap.drawPixmap(SpriteSheetPixmap.this, x, y, xIndex * spriteWidth, yIndex * spriteHeight, spriteWidth, spriteHeight);
        }

        public void drawTo(Pixmap pixmap, int destOffsetX, int destOffsetY, int srcX, int srcY, int width, int height) {
            pixmap.drawPixmap(SpriteSheetPixmap.this, destOffsetX + srcX, destOffsetY + srcY, xIndex * spriteWidth + srcX, yIndex * spriteHeight + srcY, width, height);
        }

        public void drawPixelTo(Pixmap pixmap, int destOffsetX, int destOffsetY, int srcX, int srcY) {
            pixmap.drawPixel(destOffsetX + srcX, destOffsetY + srcY, getPixel(srcX, srcY));
        }
    }

    private int spriteWidth;
    private int spriteHeight;

    private SpriteSheetPixmap(int width, int height, int spriteWidth, int spriteHeight, Format format) {
        super(width, height, format);
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
    }

    public SpriteSheetPixmap(byte[] data, int spriteWidth, int spriteHeight) {
        super(data, 0, data.length);
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
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

    public static SpriteSheetPixmap singleSprite(int width, int height, Format format) {
        return new SpriteSheetPixmap(width, height, width, height, format);
    }

}
