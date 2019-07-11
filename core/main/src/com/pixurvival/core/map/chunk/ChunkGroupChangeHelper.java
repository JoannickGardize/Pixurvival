package com.pixurvival.core.map.chunk;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.pixurvival.core.util.DoubleBufferedValue;
import com.pixurvival.core.util.Vector2;

public class ChunkGroupChangeHelper {

	private ChunkGroupRectangle rectangle = new ChunkGroupRectangle();
	private DoubleBufferedValue<Set<ChunkPosition>> chunkPositions = new DoubleBufferedValue<>(HashSet::new);

	public void move(Vector2 center, double halfSquareLength, Consumer<ChunkPosition> newPositionAction, Consumer<ChunkPosition> oldPositionAction) {
		if (!rectangle.set(center, halfSquareLength)) {
			return;
		}
		chunkPositions.swap();
		Set<ChunkPosition> currentSet = chunkPositions.getCurrentValue();
		Set<ChunkPosition> previousSet = chunkPositions.getPreviousValue();
		currentSet.clear();
		rectangle.forEachChunkPosition(position -> {
			currentSet.add(position);
			if (!previousSet.remove(position)) {
				newPositionAction.accept(position);
			}
		});
		previousSet.forEach(oldPositionAction::accept);
	}

	public boolean contains(ChunkPosition position) {
		return chunkPositions.getCurrentValue().contains(position);
	}
}
