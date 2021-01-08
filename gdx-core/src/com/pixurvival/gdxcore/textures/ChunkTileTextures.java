package com.pixurvival.gdxcore.textures;

import com.badlogic.gdx.graphics.Texture;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.Setter;

public class ChunkTileTextures {

	private @Getter Chunk chunk;

	private Texture[][] tileTextures;

	private @Getter @Setter long checkTimeStamp;

	public ChunkTileTextures(Chunk chunk) {
		this.chunk = chunk;
		tileTextures = new Texture[GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE][];
	}

	public ChunkTileTextures(ChunkTileTextures toCopy) {
		this(toCopy.getChunk());
		System.arraycopy(toCopy.tileTextures, 0, tileTextures, 0, tileTextures.length);
	}

	public void setTexturesAtLocal(int x, int y, Texture[] textures) {
		tileTextures[y * GameConstants.CHUNK_SIZE + x] = textures;
	}

	public Texture[] getTexturesAtLocal(int x, int y) {
		return tileTextures[y * GameConstants.CHUNK_SIZE + x];
	}

	public Texture[] getTexturesAt(int x, int y) {
		return getTexturesAtLocal(x - chunk.getOffsetX(), y - chunk.getOffsetY());
	}
}
