package com.pixurvival.core.map.generator;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.Chunk;

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
        buildTiles(chunk);
        buildStructures(chunk);
        mapProvider.getPostProcessors().forEach(p -> p.apply(chunk));
        return chunk;
    }

    private void buildTiles(Chunk chunk) {
        for (int cx = 0; cx < GameConstants.CHUNK_SIZE; cx++) {
            for (int cy = 0; cy < GameConstants.CHUNK_SIZE; cy++) {
                chunk.set(cx, cy, world.getMap().getMapTilesById()[mapProvider
                        .getTileAt(chunk.getPosition().getX() * GameConstants.CHUNK_SIZE + cx, chunk.getPosition().getY() * GameConstants.CHUNK_SIZE + cy).getId()]);
            }
        }
    }

    private void buildStructures(Chunk chunk) {
        int x = chunk.getPosition().getX();
        int y = chunk.getPosition().getY();
        for (int cx = 0; cx < GameConstants.CHUNK_SIZE; cx++) {
            // Desceding Y to put them in the right order for drawing
            // (pre-ordered optimization)
            for (int cy = GameConstants.CHUNK_SIZE - 1; cy >= 0; cy--) {
                MapTile mapTile = chunk.tileAtLocal(cx, cy);
                Structure structure = mapProvider.getStructureAt(x * GameConstants.CHUNK_SIZE + cx, y * GameConstants.CHUNK_SIZE + cy, mapTile.getTileDefinition());
                if (structure != null && cx <= GameConstants.CHUNK_SIZE - structure.getDimensions().getWidth() && cy <= GameConstants.CHUNK_SIZE - structure.getDimensions().getHeight()
                        && chunk.isEmptyLocal(cx, cy, structure.getDimensions().getWidth(), structure.getDimensions().getHeight())) {
                    chunk.addStructureSilently(structure, x * GameConstants.CHUNK_SIZE + cx, y * GameConstants.CHUNK_SIZE + cy).initiliazeNewlyCreated();
                }
            }
        }
    }
}
