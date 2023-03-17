package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.gdxcore.util.DrawUtils;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ChunkTextureManager implements TiledMapListener {

    // TODO avoid this map by using the tiledmap chunk map (by giving to Chunk a customData field)
    private final @Getter Map<ChunkPosition, ChunkTexture> chunkTextures = new HashMap<>();
    private Queue<ChunkTexture> chunkTextureQueue = new ArrayDeque<>();
    private Map<Integer, ChunkTexture> monoTileTextures = new HashMap<>();

    public void update(Stage worldStage) {
        if (chunkTextureQueue.isEmpty()) {
            DrawUtils.foreachChunksInScreen(worldStage, 3, chunk -> {
                ChunkTexture chunkTexture = chunkTextures.get(chunk.getPosition());
                if (chunkTexture == null) {
                    if (chunk.isMonoTile()) {
                        handleMonoTileChunk(chunk);
                    } else {
                        chunkTextureQueue.add(new ChunkTexture(chunk));
                    }
                }
            });
        }
        // Load chunk texture one by one to avoid freeze
        ChunkTexture chunkTexture = chunkTextureQueue.poll();
        if (chunkTexture == null) {
            return;
        }
        ChunkPosition position = chunkTexture.getChunk().getPosition();
        chunkTexture.buildNextFrame();
        chunkTextures.put(position, chunkTexture);
        if (chunkTexture.needsBuild()) {
            chunkTextureQueue.add(chunkTexture);
        }
    }

    private void handleMonoTileChunk(Chunk chunk) {
        Integer id = chunk.tileAtLocal(0, 0).getTileDefinition().getId();
        ChunkTexture monoTexture = monoTileTextures.get(id);
        if (monoTexture != null) {
            chunkTextures.put(chunk.getPosition(), monoTexture);
        } else {
            monoTexture = new ChunkTexture(chunk);
            chunkTextureQueue.add(monoTexture);
            monoTileTextures.put(id, monoTexture);
        }
    }

    public void dispose() {
        chunkTextures.values().forEach(ChunkTexture::dispose);
        monoTileTextures.values().forEach(ChunkTexture::dispose);
    }


    @Override
    public void chunkLoaded(Chunk chunk) {

    }

    @Override
    public void chunkUnloaded(Chunk chunk) {
        ChunkTexture ctt;
        synchronized (chunkTextures) {
            ctt = chunkTextures.remove(chunk.getPosition());
            if (ctt != null && !chunk.isMonoTile()) {
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
