package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.sprite.Frame;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.SpriteSheetPixmap.Region;
import com.pixurvival.gdxcore.util.DrawUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ChunkTileTexturesManager implements TiledMapListener {

    private TileTextureKey tileTextureKey = new TileTextureKey();
    private Map<TileTextureKey, Texture[]> tileTextures = new HashMap<>();

    private final @Getter Map<ChunkPosition, ChunkTileTextures> chunkTileTextureMap = new HashMap<>();
    private List<Supplier<Texture>> waitingTextures = new ArrayList<>();
    private List<Texture> producedTextures = new ArrayList<>();
    private @Setter boolean running = true;
    private BlockingDeque<Chunk> chunkQueue = new LinkedBlockingDeque<>();

    public ChunkTileTexturesManager() {
        ContentPackAssets contentPackTextures = PixurvivalGame.getContentPackTextures();
        for (Tile tile : PixurvivalGame.getWorld().getContentPack().getTiles()) {
            tileTextures.put(TileTextureKey.ofAll(tile), contentPackTextures.getTileTextures(tile));
        }
        Thread t = new Thread(this::run, "Chunk-texturer");
        t.setPriority(3);
        t.start();
    }

    public void update(Stage worldStage) {
        DrawUtils.foreachChunksInScreen(worldStage, 3, chunk -> {
            try {
                chunkQueue.put(chunk);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        synchronized (waitingTextures) {
            if (!waitingTextures.isEmpty()) {
                waitingTextures.forEach(s -> producedTextures.add(s.get()));
                waitingTextures.clear();
                waitingTextures.notifyAll();
            }
        }
    }

    public void dispose() {
        tileTextures.values().stream().flatMap(Arrays::stream).forEach(Texture::dispose);
    }

    private void run() {
        try {
            while (running) {
                Chunk c = chunkQueue.poll(500, TimeUnit.MILLISECONDS);
                if (c != null) {
                    ChunkTileTextures chunkTileTextures;
                    synchronized (chunkTileTextureMap) {
                        chunkTileTextures = chunkTileTextureMap.get(c.getPosition());
                    }
                    if (chunkTileTextures == null) {
                        loadChunkTileTextures(c);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void loadChunkTileTextures(Chunk chunk) {
        TiledMap map = chunk.getMap();
        Chunk topChunk;
        Chunk rightChunk;
        Chunk bottomChunk;
        Chunk leftChunk;
        Chunk topLeftChunk;
        Chunk topRightChunk;
        Chunk bottomRightChunk;
        Chunk bottomLeftChunk;
        ChunkPosition chunkPosition = chunk.getPosition();
        ChunkTileTextures topTextures = null;
        ChunkTileTextures rightTextures = null;
        ChunkTileTextures bottomTextures = null;
        ChunkTileTextures leftTextures = null;
        synchronized (map) {
            topChunk = map.chunkAt(chunkPosition.add(0, 1));
            rightChunk = map.chunkAt(chunkPosition.add(1, 0));
            bottomChunk = map.chunkAt(chunkPosition.add(0, -1));
            leftChunk = map.chunkAt(chunkPosition.add(-1, 0));
            topLeftChunk = map.chunkAt(chunkPosition.add(-1, 1));
            topRightChunk = map.chunkAt(chunkPosition.add(1, 1));
            bottomRightChunk = map.chunkAt(chunkPosition.add(1, -1));
            bottomLeftChunk = map.chunkAt(chunkPosition.add(-1, -1));
        }
        synchronized (chunkTileTextureMap) {
            if (topChunk != null) {
                topTextures = chunkTileTextureMap.get(topChunk.getPosition());
            }
            if (rightChunk != null) {
                rightTextures = chunkTileTextureMap.get(rightChunk.getPosition());
            }
            if (bottomChunk != null) {
                bottomTextures = chunkTileTextureMap.get(bottomChunk.getPosition());
            }
            if (leftChunk != null) {
                leftTextures = chunkTileTextureMap.get(leftChunk.getPosition());
            }
        }

        ChunkTileTextures chunkTileTextures = new ChunkTileTextures(chunk);

        setTopLine(chunk, topChunk, chunkTileTextures);
        setBottomLine(chunk, bottomChunk, chunkTileTextures);
        setLeftLine(chunk, leftChunk, chunkTileTextures);
        setRightLine(chunk, rightChunk, chunkTileTextures);
        setTopLeftTile(chunk, topChunk, leftChunk, chunkTileTextures);
        setTopRightTile(chunk, topChunk, rightChunk, chunkTileTextures);
        setBottomRightTile(chunk, bottomChunk, rightChunk, chunkTileTextures);
        setBottomLeftTile(chunk, bottomChunk, leftChunk, chunkTileTextures);
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            for (int y = 1; y < GameConstants.CHUNK_SIZE - 1; y++) {
                Tile middle = chunk.tileAtLocal(x, y).getTileDefinition();
                Tile top = chunk.tileAtLocal(x, y + 1).getTileDefinition();
                Tile right = chunk.tileAtLocal(x + 1, y).getTileDefinition();
                Tile bottom = chunk.tileAtLocal(x, y - 1).getTileDefinition();
                Tile left = chunk.tileAtLocal(x - 1, y).getTileDefinition();
                chunkTileTextures.setTexturesAtLocal(x, y, getOrCreateTextures(middle, top, right, bottom, left));
            }
        }
        put(chunkTileTextures);

        if (topTextures != null) {
            ChunkTileTextures updatedTileTextures = new ChunkTileTextures(topTextures);
            setBottomLine(topChunk, chunk, updatedTileTextures);
            if (topLeftChunk != null) {
                setBottomLeftTile(topChunk, chunk, topLeftChunk, updatedTileTextures);
            }
            if (topRightChunk != null) {
                setBottomRightTile(topChunk, chunk, topRightChunk, updatedTileTextures);
            }
            put(updatedTileTextures);
        }

        if (rightTextures != null) {
            ChunkTileTextures updatedTileTextures = new ChunkTileTextures(rightTextures);
            setLeftLine(rightChunk, chunk, updatedTileTextures);
            if (topRightChunk != null) {
                setTopLeftTile(rightChunk, topRightChunk, chunk, updatedTileTextures);
            }
            if (bottomRightChunk != null) {
                setBottomLeftTile(rightChunk, bottomRightChunk, chunk, updatedTileTextures);
            }
            put(updatedTileTextures);
        }

        if (bottomTextures != null) {
            ChunkTileTextures updatedTileTextures = new ChunkTileTextures(bottomTextures);
            setTopLine(bottomChunk, chunk, updatedTileTextures);
            if (bottomRightChunk != null) {
                setTopRightTile(bottomChunk, chunk, bottomRightChunk, updatedTileTextures);
            }
            if (bottomLeftChunk != null) {
                setTopLeftTile(bottomChunk, chunk, bottomLeftChunk, updatedTileTextures);
            }
            put(updatedTileTextures);
        }

        if (leftTextures != null) {
            ChunkTileTextures updatedTileTextures = new ChunkTileTextures(leftTextures);
            setRightLine(leftChunk, chunk, updatedTileTextures);
            if (bottomLeftChunk != null) {
                setBottomRightTile(leftChunk, bottomLeftChunk, chunk, updatedTileTextures);
            }
            if (topLeftChunk != null) {
                setTopRightTile(leftChunk, topLeftChunk, chunk, updatedTileTextures);
            }
            put(updatedTileTextures);
        }
    }

    private void put(ChunkTileTextures chunkTileTextures) {
        synchronized (chunkTileTextureMap) {
            chunkTileTextureMap.put(chunkTileTextures.getChunk().getPosition(), chunkTileTextures);
        }
    }

    private void setBottomLeftTile(Chunk chunk, Chunk bottomChunk, Chunk leftChunk, ChunkTileTextures chunkTileTextures) {
        Tile middle = chunk.tileAtLocal(0, 0).getTileDefinition();
        Tile top = chunk.tileAtLocal(0, 1).getTileDefinition();
        Tile right = chunk.tileAtLocal(1, 0).getTileDefinition();
        Tile bottom = tileAtOrDefault(bottomChunk, 0, GameConstants.CHUNK_SIZE - 1, middle);
        Tile left = tileAtOrDefault(leftChunk, GameConstants.CHUNK_SIZE - 1, 0, middle);
        chunkTileTextures.setTexturesAtLocal(0, 0, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setBottomRightTile(Chunk chunk, Chunk bottomChunk, Chunk rightChunk, ChunkTileTextures chunkTileTextures) {
        Tile middle = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, 0).getTileDefinition();
        Tile top = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, 1).getTileDefinition();
        Tile right = tileAtOrDefault(rightChunk, 0, 0, middle);
        Tile bottom = tileAtOrDefault(bottomChunk, GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1, middle);
        Tile left = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 2, 0).getTileDefinition();
        chunkTileTextures.setTexturesAtLocal(GameConstants.CHUNK_SIZE - 1, 0, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setTopRightTile(Chunk chunk, Chunk topChunk, Chunk rightChunk, ChunkTileTextures chunkTileTextures) {
        Tile middle = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        Tile top = tileAtOrDefault(topChunk, GameConstants.CHUNK_SIZE - 1, 0, middle);
        Tile right = tileAtOrDefault(rightChunk, 0, GameConstants.CHUNK_SIZE - 1, middle);
        Tile bottom = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
        Tile left = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 2, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        chunkTileTextures.setTexturesAtLocal(GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setTopLeftTile(Chunk chunk, Chunk topChunk, Chunk leftChunk, ChunkTileTextures chunkTileTextures) {
        Tile middle = chunk.tileAtLocal(0, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        Tile top = tileAtOrDefault(topChunk, 0, 0, middle);
        Tile right = chunk.tileAtLocal(1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        Tile bottom = chunk.tileAtLocal(0, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
        Tile left = tileAtOrDefault(leftChunk, GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1, middle);
        chunkTileTextures.setTexturesAtLocal(0, GameConstants.CHUNK_SIZE - 1, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setRightLine(Chunk chunk, Chunk rightChunk, ChunkTileTextures chunkTileTextures) {
        for (int y = 1; y < GameConstants.CHUNK_SIZE - 1; y++) {
            Tile middle = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, y).getTileDefinition();
            Tile top = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, y + 1).getTileDefinition();
            Tile right = tileAtOrDefault(rightChunk, 0, y, middle);
            Tile bottom = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, y - 1).getTileDefinition();
            Tile left = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 2, y).getTileDefinition();
            chunkTileTextures.setTexturesAtLocal(GameConstants.CHUNK_SIZE - 1, y, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private void setLeftLine(Chunk chunk, Chunk leftChunk, ChunkTileTextures chunkTileTextures) {
        // left line
        for (int y = 1; y < GameConstants.CHUNK_SIZE - 1; y++) {
            Tile middle = chunk.tileAtLocal(0, y).getTileDefinition();
            Tile top = chunk.tileAtLocal(0, y + 1).getTileDefinition();
            Tile right = chunk.tileAtLocal(1, y).getTileDefinition();
            Tile bottom = chunk.tileAtLocal(0, y - 1).getTileDefinition();
            Tile left = tileAtOrDefault(leftChunk, GameConstants.CHUNK_SIZE - 1, y, middle);
            chunkTileTextures.setTexturesAtLocal(0, y, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private void setBottomLine(Chunk chunk, Chunk bottomChunk, ChunkTileTextures chunkTileTextures) {
        // bottom line
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            Tile middle = chunk.tileAtLocal(x, 0).getTileDefinition();
            Tile top = chunk.tileAtLocal(x, 1).getTileDefinition();
            Tile right = chunk.tileAtLocal(x + 1, 0).getTileDefinition();
            Tile bottom = tileAtOrDefault(bottomChunk, x, GameConstants.CHUNK_SIZE - 1, middle);
            Tile left = chunk.tileAtLocal(x - 1, 0).getTileDefinition();
            chunkTileTextures.setTexturesAtLocal(x, 0, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private void setTopLine(Chunk chunk, Chunk topChunk, ChunkTileTextures chunkTileTextures) {
        // top line
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            Tile middle = chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            Tile top = tileAtOrDefault(topChunk, x, 0, middle);
            Tile right = chunk.tileAtLocal(x + 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            Tile bottom = chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
            Tile left = chunk.tileAtLocal(x - 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            chunkTileTextures.setTexturesAtLocal(x, GameConstants.CHUNK_SIZE - 1, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private Tile tileAtOrDefault(Chunk chunk, int x, int y, Tile defaultTile) {
        return chunk == null ? defaultTile : chunk.tileAtLocal(x, y).getTileDefinition();
    }

    private Texture[] getOrCreateTextures(Tile middle, Tile top, Tile right, Tile bottom, Tile left) {
        setTextureKey(middle, top, right, bottom, left);
        return getOrCreateTextures();
    }

    private void setTextureKey(Tile middle, Tile top, Tile right, Tile bottom, Tile left) {
        tileTextureKey.setMiddle(middle);
        tileTextureKey.setTopLeft(cornerTile(middle, top, left));
        tileTextureKey.setTopRight(cornerTile(middle, top, right));
        tileTextureKey.setBottomRight(cornerTile(middle, bottom, right));
        tileTextureKey.setBottomLeft(cornerTile(middle, bottom, left));
    }

    private Tile cornerTile(Tile middle, Tile neighbor1, Tile neighbor2) {
        return neighbor1 == neighbor2 ? neighbor1 : middle;
    }

    private Texture[] getOrCreateTextures() {
        Texture[] textures = tileTextures.get(tileTextureKey);
        if (textures == null) {
            int maxFrames = getMaxFrameSize();
            textures = new Texture[maxFrames];
            synchronized (waitingTextures) {
                for (int i = 0; i < maxFrames; i++) {
                    Region middle = getRegionFor(tileTextureKey.getMiddle(), i);
                    Region topLeft = getRegionFor(tileTextureKey.getTopLeft(), i);
                    Region topRight = getRegionFor(tileTextureKey.getTopRight(), i);
                    Region bottomRight = getRegionFor(tileTextureKey.getBottomRight(), i);
                    Region bottomLeft = getRegionFor(tileTextureKey.getBottomLeft(), i);
                    waitingTextures.add(() -> TileCutterUtil.cut(middle, topLeft, topRight, bottomRight, bottomLeft));
                }
                try {
                    while (!waitingTextures.isEmpty()) {
                        waitingTextures.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                for (int i = 0; i < maxFrames; i++) {
                    textures[i] = producedTextures.get(i);
                }
                producedTextures.clear();
            }
            tileTextures.put(new TileTextureKey(tileTextureKey), textures);
        }
        return textures;
    }

    private int getMaxFrameSize() {
        int max = tileTextureKey.getMiddle().getFrames().size();
        if (tileTextureKey.getTopLeft().getFrames().size() > max) {
            max = tileTextureKey.getTopLeft().getFrames().size();
        }
        if (tileTextureKey.getTopRight().getFrames().size() > max) {
            max = tileTextureKey.getTopRight().getFrames().size();
        }
        if (tileTextureKey.getBottomRight().getFrames().size() > max) {
            max = tileTextureKey.getBottomRight().getFrames().size();
        }
        if (tileTextureKey.getBottomLeft().getFrames().size() > max) {
            max = tileTextureKey.getBottomLeft().getFrames().size();
        }
        return max;
    }

    private Region getRegionFor(Tile tile, int frameIndex) {
        Frame frame = tile.getFrames().get(frameIndex % tile.getFrames().size());
        return PixurvivalGame.getContentPackTextures().getTilePixmap(tileTextureKey.getMiddle()).getRegion(frame.getX(), frame.getY());
    }

    @Override
    public void chunkLoaded(Chunk chunk) {

    }

    @Override
    public void chunkUnloaded(Chunk chunk) {
        ChunkTileTextures ctt;
        synchronized (chunkTileTextureMap) {
            ctt = chunkTileTextureMap.remove(chunk.getPosition());
        }
    }

    @Override
    public void structureChanged(StructureEntity mapStructure, StructureUpdate structureUpdate) {

    }

    @Override
    public void structureAdded(StructureEntity mapStructure) {

    }

    @Override
    public void structureRemoved(StructureEntity mapStructure) {

    }

    @Override
    public void entityEnterChunk(ChunkPosition previousPosition, Entity e) {

    }
}
