package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementList;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.generator.ChunkPostProcessor;
import com.pixurvival.core.map.generator.RemoveStuckStructuresPostProcessor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class ProcedurallyGeneratedMapProvider extends MapProvider {

    private static final long serialVersionUID = 1L;

    @Valid
    @ElementList(value = Heightmap.class)
    private List<Heightmap> heightmaps = new ArrayList<>();

    @ElementReference
    private Tile defaultTile;

    @Valid
    private List<TileGenerator> tileGenerators = new ArrayList<>();

    @Valid
    private List<StructureGenerator> structureGenerators = new ArrayList<>();

    private transient Random chunkRandom;

    private transient @Getter List<ChunkPostProcessor> postProcessors;

    @Override
    public void initialize(World world) {
        Random random = new Random(world.getSeed());
        for (Heightmap heightmap : heightmaps) {
            heightmap.initialiaze(random.nextLong());
        }
        postProcessors = Collections.singletonList(new RemoveStuckStructuresPostProcessor());
    }

    @Override
    public void beginChunk(long seed, ChunkPosition chunkPosition) {
        chunkRandom = new Random(seed << 32 ^ (long) chunkPosition.getX() << 16 ^ chunkPosition.getY());

    }

    @Override
    public Tile getTileAt(int x, int y) {
        for (TileGenerator tileGenerator : tileGenerators) {
            if (tileGenerator.test(x, y)) {
                return tileGenerator.getTileAt(x, y);
            }
        }
        return defaultTile;
    }

    @Override
    public Structure getStructureAt(int x, int y, Tile tile) {
        for (StructureGenerator structureGenerator : structureGenerators) {
            if (structureGenerator.test(x, y)) {
                return structureGenerator.next(tile, chunkRandom);
            }
        }
        return null;
    }

}
