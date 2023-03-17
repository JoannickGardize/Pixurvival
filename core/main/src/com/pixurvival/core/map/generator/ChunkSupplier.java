package com.pixurvival.core.map.generator;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.map.ProcedurallyGeneratedMapProvider;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.Chunk;

import java.util.Arrays;

/**
 * Uses a {@link MapProvider} of a {@link World} to produce new {@link Chunk}s
 * on demand.
 *
 * @author SharkHendrix
 */
public class ChunkSupplier {

    private World world;
    private MapProvider mapProvider;

    public ChunkSupplier(World world) {
        this.world = world;
        mapProvider = world.getGameMode().getMapProvider();
        mapProvider.initialize(world);
    }

    public Chunk get(int x, int y) {
        Chunk chunk = new Chunk(world.getMap(), x, y);
        mapProvider.beginChunk(world.getSeed(), chunk.getPosition());
        build(chunk);
        mapProvider.getPostProcessors().forEach(p -> p.apply(chunk));
        chunk.computeMetadata();
        return chunk;
    }

    private void build(Chunk chunk) {
        int x = chunk.getPosition().getX();
        int y = chunk.getPosition().getY();
        float[] run = mapProvider instanceof ProcedurallyGeneratedMapProvider ?
                new float[((ProcedurallyGeneratedMapProvider) mapProvider).getHeightmaps().size()] : null;

        for (int cx = GameConstants.CHUNK_SIZE - 1; cx >= 0; cx--) {
            // reverse x because we need generated tiles under structures first for the chunk.isEmptyLocal() call
            for (int cy = GameConstants.CHUNK_SIZE - 1; cy >= 0; cy--) {
                // reverse y for same reason + for ordering structures in the right way for render (sort() makes use of pre-ordered groups)
                MapTile mapTile = buildTile(chunk, run, cx, cy);
                Structure structure = mapProvider.getStructureAt(x * GameConstants.CHUNK_SIZE + cx, y * GameConstants.CHUNK_SIZE + cy, mapTile.getTileDefinition(), run);
                if (structure != null && cx <= GameConstants.CHUNK_SIZE - structure.getDimensions().getWidth() && cy <= GameConstants.CHUNK_SIZE - structure.getDimensions().getHeight()
                        && chunk.isEmptyLocal(cx, cy, structure.getDimensions().getWidth(), structure.getDimensions().getHeight())) {
                    chunk.addStructureSilently(structure, x * GameConstants.CHUNK_SIZE + cx, y * GameConstants.CHUNK_SIZE + cy).initiliazeNewlyCreated();
                }
            }
        }

        // Generate overflowing tiles
        for (int i = 0; i < GameConstants.CHUNK_SIZE; i++) {
            buildTile(chunk, run, -1, i);
            buildTile(chunk, run, GameConstants.CHUNK_SIZE, i);
            buildTile(chunk, run, i, -1);
            buildTile(chunk, run, i, GameConstants.CHUNK_SIZE);
        }
        buildTile(chunk, run, -1, -1);
        buildTile(chunk, run, GameConstants.CHUNK_SIZE, -1);
        buildTile(chunk, run, -1, GameConstants.CHUNK_SIZE);
        buildTile(chunk, run, GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE);
    }

    private MapTile buildTile(Chunk chunk, float[] run, int cx, int cy) {
        Arrays.fill(run, -1);
        MapTile mapTile = world.getMap().getMapTilesById()[mapProvider
                .getTileAt(chunk.getPosition().getX() * GameConstants.CHUNK_SIZE + cx, chunk.getPosition().getY() * GameConstants.CHUNK_SIZE + cy, run).getId()];
        chunk.set(cx, cy, mapTile);
        return mapTile;
    }
}
