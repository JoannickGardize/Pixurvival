package com.pixurvival.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class PlayerSession {

	private @NonNull @Getter PlayerConnection connection;
	private @Getter Set<ChunkPosition> knownPositions = new HashSet<>();
	private List<CompressedChunk> chunksToSend = new ArrayList<>();
	private List<StructureUpdate> structureUpdatesToSend = new ArrayList<>();
	private Set<ChunkPosition> missingChunks = new HashSet<>();
	private Set<ChunkPosition> newPositions = new HashSet<>();
	private Set<ChunkPosition> oldPositions = new HashSet<>();
	private @Getter @Setter Map<Long, SpectatorSession> spectators = new HashMap<>();

	public void addNewPosition(ChunkPosition position) {
		newPositions.add(position);
		oldPositions.remove(position);
	}

	public void addOldPosition(ChunkPosition position) {
		oldPositions.add(position);
		newPositions.remove(position);
	}

	public void foreachOldPosition(Consumer<ChunkPosition> action) {
		oldPositions.forEach(action);
	}

	public void addChunkIfNotKnown(Chunk chunk) {
		if (knownPositions.add(chunk.getPosition())) {
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

	public boolean isMissingAndRemove(ChunkPosition position) {
		return missingChunks.remove(position);
	}

	public boolean isNewPosition(ChunkPosition position) {
		return newPositions.contains(position);
	}

	public boolean isOldPosition(ChunkPosition position) {
		return oldPositions.contains(position);
	}

	public void clearPositionChanges() {
		newPositions.clear();
		oldPositions.clear();
	}

	public void extractChunksToSend(List<CompressedChunk> list) {
		list.addAll(chunksToSend);
		chunksToSend.clear();
	}

	public void extractStructureUpdatesToSend(List<StructureUpdate> list) {
		list.addAll(structureUpdatesToSend);
		structureUpdatesToSend.clear();
	}
}
