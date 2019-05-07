package com.pixurvival.core.map;

import com.pixurvival.core.livingEntity.PlayerEntity;

public interface PlayerMapEventListener {

	void enterVision(PlayerEntity entity, ChunkPosition position);

	void exitVision(PlayerEntity entity, ChunkPosition position);

	void enterChunk(PlayerEntity e);
}
