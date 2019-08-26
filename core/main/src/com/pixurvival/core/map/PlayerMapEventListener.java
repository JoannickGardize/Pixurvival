package com.pixurvival.core.map;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.chunk.ChunkPosition;

public interface PlayerMapEventListener {

	void enterVision(PlayerEntity entity, ChunkPosition position);

	void exitVision(PlayerEntity entity, ChunkPosition position);
}
