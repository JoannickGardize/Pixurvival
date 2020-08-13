package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.Body;
import com.pixurvival.core.CustomDataHolder;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MapStructure implements Body, CustomDataHolder {

	@FunctionalInterface
	private static interface StructureSupplier {
		MapStructure apply(Chunk chunk, Structure structure, int x, int y);
	}

	private Chunk chunk;
	private Structure definition;
	private int tileX;
	private int tileY;
	private Vector2 position;
	private @Setter Object customData;

	private @Setter long creationTime;

	public MapStructure(Chunk chunk, Structure definition, int x, int y) {
		this.chunk = chunk;
		this.definition = definition;
		tileX = x;
		tileY = y;
		position = new Vector2(x + definition.getDimensions().getWidth() / 2f, y + definition.getDimensions().getHeight() / 2f);

		if (chunk != null) {
			World world = chunk.getMap().getWorld();
			if (world.isServer() && definition.getDuration() > 0) {
				creationTime = chunk.getMap().getWorld().getTime().getTimeMillis();
				world.getActionTimerManager().addActionTimer(new RemoveDurationStructureAction(x, y), definition.getDuration());
			}
		}
	}

	@Override
	public float getHalfWidth() {
		return definition.getDimensions().getWidth() / 2f;
	}

	@Override
	public float getHalfHeight() {
		return definition.getDimensions().getHeight() / 2f;
	}

	public int getWidth() {
		return definition.getDimensions().getWidth();
	}

	public int getHeight() {
		return definition.getDimensions().getHeight();
	}

	/**
	 * Override this to write data that must be sent to the client
	 * 
	 * @param buffer
	 */
	public void writeData(ByteBuffer buffer) {
		if (definition.getDuration() > 0) {
			buffer.putLong(creationTime);
		}
	}

	/**
	 * Override this to read data sent by the server
	 * 
	 * @param buffer
	 */
	public void applyData(ByteBuffer buffer) {
		if (definition.getDuration() > 0) {
			creationTime = buffer.getLong();
		}
	}

	/**
	 * Override this to return a structure update
	 * 
	 * @return
	 */
	public StructureUpdate getUpdate() {
		return null;
	}

	@Override
	public World getWorld() {
		return getChunk().getMap().getWorld();
	}
}
