package com.pixurvival.server;

import java.util.List;

import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.server.util.AveragePerSecondHelper;

import lombok.Getter;
import lombok.Setter;

public class NetworkStats implements NetworkListener {
	private @Getter AveragePerSecondHelper entityLengthAverage = new AveragePerSecondHelper();
	private @Getter AveragePerSecondHelper compressedChunkLengthAverage = new AveragePerSecondHelper();
	private @Setter Runnable changedAction = () -> {
	};

	@Override
	public void sent(WorldUpdate worldUpdate) {
		int entityLength = worldUpdate.getEntityUpdateLength();
		List<CompressedChunk> compressedChunks = worldUpdate.getCompressedChunks();
		int totalCompressedChunks = 0;
		if (compressedChunks != null) {
			for (CompressedChunk compressedChunk : compressedChunks) {
				totalCompressedChunks += compressedChunk.getData().length;
			}
		}
		if (entityLengthAverage.add(entityLength) || compressedChunkLengthAverage.add(totalCompressedChunks)) {
			changedAction.run();
		}
	}
}
