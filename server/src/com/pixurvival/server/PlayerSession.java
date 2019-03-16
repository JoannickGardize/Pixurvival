package com.pixurvival.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.CompressedChunk;
import com.pixurvival.core.map.ChunkPosition;
import com.pixurvival.core.message.StructureUpdate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerSession {

	private @NonNull @Getter PlayerConnection connection;
	private Set<ChunkPosition> knownPositions = new HashSet<>();
	private List<CompressedChunk> chunksToSend = new ArrayList<>();
	private List<StructureUpdate> structureUpdatesToSend = new ArrayList<>();
	private Set<ChunkPosition> missingChunks = new HashSet<>();

	public void addChunkIfNotKnown(Chunk chunk) {
		if (!knownPositions.contains(chunk.getPosition())) {
			knownPositions.add(chunk.getPosition());
			chunksToSend.add(chunk.getCompressed());
		}
	}

	public void invalidateChunk(ChunkPosition position) {
		knownPositions.remove(position);
	}

	public void addMissingChunk(ChunkPosition position) {
		missingChunks.add(position);
	}

	public void addStructureUpdate(StructureUpdate structureUpdate) {
		structureUpdatesToSend.add(structureUpdate);
	}

	public boolean isMissing(ChunkPosition position) {
		return missingChunks.remove(position);
	}

	public CompressedChunk[] pollChunksToSend() {
		CompressedChunk[] result = chunksToSend.toArray(new CompressedChunk[chunksToSend.size()]);
		chunksToSend.clear();
		return result;
	}

	public StructureUpdate[] pollStructureUpdatesToSend() {
		StructureUpdate[] result = structureUpdatesToSend.toArray(new StructureUpdate[structureUpdatesToSend.size()]);
		structureUpdatesToSend.clear();
		return result;
	}
}
