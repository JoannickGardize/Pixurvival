package com.pixurvival.gdxcore.drawer;

import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkManagerPlugin;

import java.util.Random;

public class StructureEntityFliper implements ChunkManagerPlugin {

    @Override
    public void chunkLoaded(Chunk chunk) {
        Random random = chunk.createFixedRandom();
        chunk.forEachStructure(ms -> {
            if (ms.getDefinition().isRandomHorizontalFlip()) {
                DrawData drawData = new DrawData();
                drawData.setFlip(random.nextBoolean());
                ms.setCustomData(drawData);
            }
        });
    }
}
