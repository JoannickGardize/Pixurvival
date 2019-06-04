package com.pixurvival.core.map.analytics;

import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.ChunkPosition;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.TiledMap;

/**
 * This class is a purpose for a fast synchronized exploration of a
 * {@link TiledMap}, exploiting the unsynchronized {@link Chunk} generation
 * system. It keeps in track the current {@link Chunk} and requests in advance
 * all neighbors {@link Chunk}, to anticipate future calls.
 * 
 * @author SharkHendrix
 *
 */
public class TiledMapCursor {

	private TiledMap tiledMap;
	private Chunk currentChunk;

	public TiledMapCursor(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	public MapTile tileAt(Position tilePosition) {
		return tileAt(tilePosition.getX(), tilePosition.getY());
	}

	public MapTile tileAt(int x, int y) {
		if (currentChunk == null || !currentChunk.containsTile(x, y)) {
			// Request and wait for the missing Chunk.
			ChunkPosition chunkPosition = ChunkPosition.fromWorldPosition(x, y);
			currentChunk = tiledMap.chunkAtStrict(chunkPosition);
			// Request all neighburs Chunks, to avoid waiting for probable
			// future calls. This greatly improves performance.
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX() - 1, chunkPosition.getY()));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX(), chunkPosition.getY() - 1));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX() + 1, chunkPosition.getY()));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX(), chunkPosition.getY() + 1));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX() - 1, chunkPosition.getY() - 1));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX() + 1, chunkPosition.getY() + 1));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX() - 1, chunkPosition.getY() + 1));
			tiledMap.requestChunk(new ChunkPosition(chunkPosition.getX() + 1, chunkPosition.getY() - 1));
		}
		return currentChunk.tileAt(x, y);
	}
}