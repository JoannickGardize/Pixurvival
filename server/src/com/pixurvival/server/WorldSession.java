package com.pixurvival.server;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.message.HarvestableStructureUpdate;

import lombok.Getter;

public class WorldSession implements TiledMapListener {

	private @Getter World world;
	private List<HarvestableStructureUpdate> structureUpdates = new ArrayList<>();
	private @Getter List<PlayerConnection> players = new ArrayList<>();

	public WorldSession(World world) {
		this.world = world;
		world.getMap().addListener(this);
	}

	@Override
	public void chunkAdded(Chunk chunk) {
	}

	@Override
	public void structureChanged(MapStructure mapStructure) {
		if (mapStructure instanceof HarvestableStructure) {
			HarvestableStructure hs = (HarvestableStructure) mapStructure;
			structureUpdates.add(new HarvestableStructureUpdate(hs.getTileX(), hs.getTileY(), hs.isHarvested()));
		}
	}

	public HarvestableStructureUpdate[] consumeStructureUpdates() {
		HarvestableStructureUpdate[] result = structureUpdates
				.toArray(new HarvestableStructureUpdate[structureUpdates.size()]);
		structureUpdates.clear();
		return result;
	}
}
