package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.Getter;

public class ChunkTexture {

    private @Getter Chunk chunk;

    private @Getter Texture[] textures;

    private int nextFrameToBuild = 0;

    public ChunkTexture(Chunk chunk) {
        this.chunk = chunk;
        textures = new Texture[chunk.getMaxTileFrameCount()];
    }

    public boolean needsBuild() {
        return chunk != null && nextFrameToBuild < chunk.getMaxTileFrameCount();
    }

    public boolean isReady() {
        return textures[0] != null;
    }

    public void buildNextFrame() {
        final int CHUNK_TEXTURE_SIZE = GameConstants.CHUNK_SIZE * GameConstants.PIXEL_PER_UNIT + 1;
        if (!needsBuild()) {
            // Happens for mono-tile textures
            return;
        }
        Pixmap pixmap = new Pixmap(CHUNK_TEXTURE_SIZE, CHUNK_TEXTURE_SIZE, Pixmap.Format.RGB888);
        pixmap.setBlending(Pixmap.Blending.None);

        for (int x = 0; x < GameConstants.CHUNK_SIZE; x++) {
            for (int y = 0; y < GameConstants.CHUNK_SIZE; y++) {
                Tile middle = chunk.tileAtLocal(x, y).getTileDefinition();
                Tile top = chunk.tileAtLocal(x, y + 1).getTileDefinition();
                Tile right = chunk.tileAtLocal(x + 1, y).getTileDefinition();
                Tile bottom = chunk.tileAtLocal(x, y - 1).getTileDefinition();
                Tile left = chunk.tileAtLocal(x - 1, y).getTileDefinition();
                buildTile(pixmap, x * GameConstants.PIXEL_PER_UNIT, CHUNK_TEXTURE_SIZE - (y + 1) * GameConstants.PIXEL_PER_UNIT,
                        nextFrameToBuild, middle, top, right, bottom, left);
            }
        }

        // Overflow pixels to avoid black lines bug
        pixmap.drawPixmap(pixmap, 0, 0, 0, 1, pixmap.getWidth(), 1);
        pixmap.drawPixmap(pixmap, pixmap.getWidth() - 1, 1, pixmap.getWidth() - 2, 1, 1, pixmap.getHeight() - 1);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        textures[nextFrameToBuild] = texture;
        for (int i = nextFrameToBuild + 1; i < textures.length; i++) {
            textures[i] = texture;
        }
        nextFrameToBuild++;
        if (!needsBuild()) {
            chunk = null;
        }
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }

    private void buildTile(Pixmap pixmap, int x, int y, int frameIndex, Tile middle, Tile top, Tile right, Tile bottom, Tile left) {
        TileCutterUtil.cut(getRegionFor(middle, frameIndex),
                getRegionFor(cornerTile(middle, top, left), frameIndex), getRegionFor(cornerTile(middle, top, right), frameIndex),
                getRegionFor(cornerTile(middle, bottom, right), frameIndex), getRegionFor(cornerTile(middle, bottom, left), frameIndex),
                pixmap, x, y);
    }

    private SpriteSheetPixmap.Region getRegionFor(Tile tile, int frameIndex) {
        Frame frame = tile.getFrames().get(frameIndex % tile.getFrames().size());
        return PixurvivalGame.getContentPackTextures().getTilePixmap(tile).getRegion(frame.getX(), frame.getY());
    }

    private Tile cornerTile(Tile middle, Tile neighbor1, Tile neighbor2) {
        return neighbor1 == neighbor2 ? neighbor1 : middle;
    }
}
