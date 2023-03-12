package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.gdxcore.PixurvivalGame;
import lombok.Getter;
import lombok.Setter;


// 9 PATCH This
public class ChunkTexture {

    private final int CHUNK_TEXTURE_SIZE = GameConstants.CHUNK_SIZE * GameConstants.PIXEL_PER_UNIT + 1;

    private static final int BUILT_CENTER = 1;
    private static final int BUILT_LEFT = 2;
    private static final int BUILT_RIGHT = 4;
    private static final int BUILT_TOP = 8;
    private static final int BUILT_BOTTOM = 16;
    private static final int BUILT_TOP_RIGHT = 32;
    private static final int BUILT_TOP_LEFT = 64;
    private static final int BUILT_BOTTOM_LEFT = 128;
    private static final int BUILT_BOTTOM_RIGHT = 256;

    private static final int BUILD_FINISHED = BUILT_CENTER | BUILT_LEFT | BUILT_RIGHT | BUILT_TOP | BUILT_BOTTOM |
            BUILT_TOP_RIGHT | BUILT_TOP_LEFT | BUILT_BOTTOM_LEFT | BUILT_BOTTOM_RIGHT;

    private @Getter Chunk chunk;

    private Pixmap pixmap;

    private @Getter Texture texture;

    private int buildFlags = 0;

    @Getter
    @Setter
    private boolean buildRequested = true;

    public ChunkTexture(Chunk chunk) {
        this.chunk = chunk;
    }

    public boolean isFullyBuilt() {
        return buildFlags == BUILD_FINISHED;
    }

    public void buildOrRebuild(Chunk topChunk) {
        buildRequested = false;
        if (isFullyBuilt()) {
            return;
        }
        if (pixmap == null) {
            pixmap = new Pixmap(CHUNK_TEXTURE_SIZE, CHUNK_TEXTURE_SIZE, Pixmap.Format.RGB888);
            pixmap.setBlending(Pixmap.Blending.None);
        }
        int initialState = buildFlags;

        if ((buildFlags & BUILT_CENTER) == 0) {
            buildCenter();
            buildFlags |= BUILT_CENTER;
        }

        if (topChunk != null && (buildFlags & BUILT_TOP) == 0) {
            buildTop(topChunk);
            buildFlags |= BUILT_TOP;
        }
        //buildFlags = BUILD_FINISHED;
        if (initialState != buildFlags) {
            if (texture != null) {
                texture.dispose();
            }
            texture = new Texture(pixmap);
            if (isFullyBuilt()) {
                pixmap.dispose();
            }
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private void buildCenter() {
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            for (int y = 1; y < GameConstants.CHUNK_SIZE - 1; y++) {
                Tile middle = chunk.tileAtLocal(x, y).getTileDefinition();
                Tile top = chunk.tileAtLocal(x, y + 1).getTileDefinition();
                Tile right = chunk.tileAtLocal(x + 1, y).getTileDefinition();
                Tile bottom = chunk.tileAtLocal(x, y - 1).getTileDefinition();
                Tile left = chunk.tileAtLocal(x - 1, y).getTileDefinition();
                buildTile(x * GameConstants.PIXEL_PER_UNIT, CHUNK_TEXTURE_SIZE - (y + 1) * GameConstants.PIXEL_PER_UNIT, middle, top, right, bottom, left);
            }
        }
    }

    private void buildTop(Chunk topChunk) {
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            Tile middle = chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            Tile top = topChunk.tileAtLocal(x, 0).getTileDefinition();
            Tile right = chunk.tileAtLocal(x + 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            Tile bottom = chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
            Tile left = chunk.tileAtLocal(x - 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            buildTile(x * GameConstants.PIXEL_PER_UNIT, 1, middle, top, right, bottom, left);
        }
    }

    public void dispose() {
        if (pixmap != null && !pixmap.isDisposed()) {
            pixmap.dispose();
        }
        if (texture != null) {
            texture.dispose();
        }
    }

    private void buildTile(int x, int y, Tile middle, Tile top, Tile right, Tile bottom, Tile left) {
        TileCutterUtil.cut(getRegionFor(middle, 0),
                getRegionFor(cornerTile(middle, top, left), 0), getRegionFor(cornerTile(middle, top, right), 0),
                getRegionFor(cornerTile(middle, bottom, right), 0), getRegionFor(cornerTile(middle, bottom, left), 0),
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
