package com.pixurvival.gdxcore.textures;

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

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ChunkTextureManager implements TiledMapListener {

    private TileTextureKey tileTextureKey = new TileTextureKey();

    // TODO avoid this map by using the tiledmap chunk map (by giving to Chunk a customData field)
    private final @Getter Map<ChunkPosition, ChunkTexture> chunkTextures = new HashMap<>();
    private Queue<ChunkTexture> chunkTextureQueue = new ArrayDeque<>();

    /*public ChunkTextureManager() {
        Thread t = new Thread(this::run, "Chunk-texturer");
        t.setPriority(3);
        t.start();
    }*/

    public void update(Stage worldStage) {
        if (chunkTextureQueue.isEmpty()) {
            DrawUtils.foreachChunksInScreen(worldStage, 4, chunk -> {
                ChunkTexture chunkTexture = chunkTextures.get(chunk.getPosition());
                if (chunkTexture == null) {
                    chunkTextureQueue.add(new ChunkTexture(chunk));
                } else if (!chunkTexture.isFullyBuilt() && !chunkTexture.isBuildRequested()) {
                    chunkTexture.setBuildRequested(true);
                    chunkTextureQueue.add(chunkTexture);
                }
            });
        }
        // Load chunk texture one by one to avoid freeze
        ChunkTexture chunkTexture = chunkTextureQueue.poll();
        if (chunkTexture == null) {
            return;
        }
        TiledMap map = PixurvivalGame.getWorld().getMap();
        ChunkPosition chunkPosition = chunkTexture.getChunk().getPosition();
        Chunk topChunk = map.chunkAt(chunkPosition.add(0, 1));
        Chunk rightChunk = map.chunkAt(chunkPosition.add(1, 0));
        Chunk bottomChunk = map.chunkAt(chunkPosition.add(0, -1));
        Chunk leftChunk = map.chunkAt(chunkPosition.add(-1, 0));
            /*Chunk topLeftChunk = map.chunkAt(chunkPosition.add(-1, 1));
            Chunk topRightChunk = map.chunkAt(chunkPosition.add(1, 1));
            Chunk bottomRightChunk = map.chunkAt(chunkPosition.add(1, -1));
            Chunk bottomLeftChunk = map.chunkAt(chunkPosition.add(-1, -1));*/

        chunkTexture.buildOrRebuild(topChunk);
        chunkTextures.put(chunkPosition, chunkTexture);
    }

    public void dispose() {
        chunkTextures.values().forEach(c -> c.dispose());
    }

    private void run() {
        /*try {
            while (running) {
                Chunk c = chunkQueue.poll(500, TimeUnit.MILLISECONDS);
                if (c != null) {
                    Texture chunkTexture;
                    synchronized (chunkTextures) {
                        chunkTexture = chunkTextures.get(c.getPosition());
                    }
                    if (chunkTexture == null) {
                        loadChunkTexture(c);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }*/
    }

    private void loadChunkTexture(Chunk chunk) {
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
        boolean topExists = false, rightExists = false, bottomExists = false, leftExists = false;
        synchronized (chunkTextures) {
            if (topChunk != null) {
                topExists = chunkTextures.containsKey(topChunk.getPosition());
            }
            if (rightChunk != null) {
                rightExists = chunkTextures.containsKey(rightChunk.getPosition());
            }
            if (bottomChunk != null) {
                bottomExists = chunkTextures.containsKey(bottomChunk.getPosition());
            }
            if (leftChunk != null) {
                leftExists = chunkTextures.containsKey(leftChunk.getPosition());
            }
        }

        ChunkTexture chunkTexture = new ChunkTexture(chunk);

        setTopLine(chunk, topChunk, chunkTexture);
        setBottomLine(chunk, bottomChunk, chunkTexture);
        setLeftLine(chunk, leftChunk, chunkTexture);
        setRightLine(chunk, rightChunk, chunkTexture);
        setTopLeftTile(chunk, topChunk, leftChunk, chunkTexture);
        setTopRightTile(chunk, topChunk, rightChunk, chunkTexture);
        setBottomRightTile(chunk, bottomChunk, rightChunk, chunkTexture);
        setBottomLeftTile(chunk, bottomChunk, leftChunk, chunkTexture);

        put(chunkTexture);

        /*if (topExists) {
            ChunkTexture updatedTileTextures = new ChunkTexture(topTextures);
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
            ChunkTexture updatedTileTextures = new ChunkTexture(rightTextures);
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
            ChunkTexture updatedTileTextures = new ChunkTexture(bottomTextures);
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
            ChunkTexture updatedTileTextures = new ChunkTexture(leftTextures);
            setRightLine(leftChunk, chunk, updatedTileTextures);
            if (bottomLeftChunk != null) {
                setBottomRightTile(leftChunk, bottomLeftChunk, chunk, updatedTileTextures);
            }
            if (topLeftChunk != null) {
                setTopRightTile(leftChunk, topLeftChunk, chunk, updatedTileTextures);
            }
            put(updatedTileTextures);
        }*/
    }

    private void put(ChunkTexture chunkTexture) {
        synchronized (chunkTextures) {
            //chunkTextures.put(chunkTexture.getChunk().getPosition(), chunkTexture);
        }
    }

    private void setBottomLeftTile(Chunk chunk, Chunk bottomChunk, Chunk leftChunk, ChunkTexture chunkTexture) {
        Tile middle = chunk.tileAtLocal(0, 0).getTileDefinition();
        Tile top = chunk.tileAtLocal(0, 1).getTileDefinition();
        Tile right = chunk.tileAtLocal(1, 0).getTileDefinition();
        Tile bottom = tileAtOrDefault(bottomChunk, 0, GameConstants.CHUNK_SIZE - 1, middle);
        Tile left = tileAtOrDefault(leftChunk, GameConstants.CHUNK_SIZE - 1, 0, middle);
        //chunkTexture.setTexturesAtLocal(0, 0, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setBottomRightTile(Chunk chunk, Chunk bottomChunk, Chunk rightChunk, ChunkTexture chunkTexture) {
        Tile middle = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, 0).getTileDefinition();
        Tile top = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, 1).getTileDefinition();
        Tile right = tileAtOrDefault(rightChunk, 0, 0, middle);
        Tile bottom = tileAtOrDefault(bottomChunk, GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1, middle);
        Tile left = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 2, 0).getTileDefinition();
        //chunkTexture.setTexturesAtLocal(GameConstants.CHUNK_SIZE - 1, 0, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setTopRightTile(Chunk chunk, Chunk topChunk, Chunk rightChunk, ChunkTexture chunkTexture) {
        Tile middle = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        Tile top = tileAtOrDefault(topChunk, GameConstants.CHUNK_SIZE - 1, 0, middle);
        Tile right = tileAtOrDefault(rightChunk, 0, GameConstants.CHUNK_SIZE - 1, middle);
        Tile bottom = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
        Tile left = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 2, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        //chunkTexture.setTexturesAtLocal(GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setTopLeftTile(Chunk chunk, Chunk topChunk, Chunk leftChunk, ChunkTexture chunkTexture) {
        Tile middle = chunk.tileAtLocal(0, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        Tile top = tileAtOrDefault(topChunk, 0, 0, middle);
        Tile right = chunk.tileAtLocal(1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
        Tile bottom = chunk.tileAtLocal(0, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
        Tile left = tileAtOrDefault(leftChunk, GameConstants.CHUNK_SIZE - 1, GameConstants.CHUNK_SIZE - 1, middle);
        //chunkTexture.setTexturesAtLocal(0, GameConstants.CHUNK_SIZE - 1, getOrCreateTextures(middle, top, right, bottom, left));
    }

    private void setRightLine(Chunk chunk, Chunk rightChunk, ChunkTexture chunkTexture) {
        for (int y = 1; y < GameConstants.CHUNK_SIZE - 1; y++) {
            Tile middle = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, y).getTileDefinition();
            Tile top = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, y + 1).getTileDefinition();
            Tile right = tileAtOrDefault(rightChunk, 0, y, middle);
            Tile bottom = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 1, y - 1).getTileDefinition();
            Tile left = chunk.tileAtLocal(GameConstants.CHUNK_SIZE - 2, y).getTileDefinition();
            //chunkTexture.setTexturesAtLocal(GameConstants.CHUNK_SIZE - 1, y, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private void setLeftLine(Chunk chunk, Chunk leftChunk, ChunkTexture chunkTexture) {
        // left line
        for (int y = 1; y < GameConstants.CHUNK_SIZE - 1; y++) {
            Tile middle = chunk.tileAtLocal(0, y).getTileDefinition();
            Tile top = chunk.tileAtLocal(0, y + 1).getTileDefinition();
            Tile right = chunk.tileAtLocal(1, y).getTileDefinition();
            Tile bottom = chunk.tileAtLocal(0, y - 1).getTileDefinition();
            Tile left = tileAtOrDefault(leftChunk, GameConstants.CHUNK_SIZE - 1, y, middle);
            // chunkTexture.setTexturesAtLocal(0, y, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private void setBottomLine(Chunk chunk, Chunk bottomChunk, ChunkTexture chunkTexture) {
        // bottom line
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            Tile middle = chunk.tileAtLocal(x, 0).getTileDefinition();
            Tile top = chunk.tileAtLocal(x, 1).getTileDefinition();
            Tile right = chunk.tileAtLocal(x + 1, 0).getTileDefinition();
            Tile bottom = tileAtOrDefault(bottomChunk, x, GameConstants.CHUNK_SIZE - 1, middle);
            Tile left = chunk.tileAtLocal(x - 1, 0).getTileDefinition();
            //chunkTexture.setTexturesAtLocal(x, 0, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private void setTopLine(Chunk chunk, Chunk topChunk, ChunkTexture chunkTexture) {
        // top line
        for (int x = 1; x < GameConstants.CHUNK_SIZE - 1; x++) {
            Tile middle = chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            Tile top = tileAtOrDefault(topChunk, x, 0, middle);
            Tile right = chunk.tileAtLocal(x + 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            Tile bottom = chunk.tileAtLocal(x, GameConstants.CHUNK_SIZE - 2).getTileDefinition();
            Tile left = chunk.tileAtLocal(x - 1, GameConstants.CHUNK_SIZE - 1).getTileDefinition();
            //chunkTexture.setTexturesAtLocal(x, GameConstants.CHUNK_SIZE - 1, getOrCreateTextures(middle, top, right, bottom, left));
        }
    }

    private Tile tileAtOrDefault(Chunk chunk, int x, int y, Tile defaultTile) {
        return chunk == null ? defaultTile : chunk.tileAtLocal(x, y).getTileDefinition();
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
        ChunkTexture ctt;
        synchronized (chunkTextures) {
            ctt = chunkTextures.remove(chunk.getPosition());
            if (ctt != null) {
                ctt.dispose();
            }
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
