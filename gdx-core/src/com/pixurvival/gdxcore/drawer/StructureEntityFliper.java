package com.pixurvival.gdxcore.drawer;

import java.util.Random;

import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkManagerPlugin;

public class StructureEntityFliper implements ChunkManagerPlugin {

	@Override
	public void chunkLoaded(Chunk chunk) {
		Random random = chunk.createFixedRandom();
		chunk.forEachStructure(ms -> {
			DrawData drawData = new DrawData();
			drawData.setFlip(random.nextBoolean());
			ms.setCustomData(drawData);
		});
	}
}
