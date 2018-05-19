package com.pixurvival.core;

import com.pixurvival.core.map.CompressedChunk;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.StructureUpdate;

import lombok.Data;

@Data
public class SyncWorldUpdate {

	private long updateId;
	private StructureUpdate[] structureUpdates;
	private PlayerData[] playerData;
	private CompressedChunk[] compressedChunks;
}
