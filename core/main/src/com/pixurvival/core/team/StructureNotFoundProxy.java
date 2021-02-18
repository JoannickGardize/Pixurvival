package com.pixurvival.core.team;

import com.pixurvival.core.World;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;

public class StructureNotFoundProxy extends FlatTeamMember {

	private long id;
	private int x;
	private int y;

	public StructureNotFoundProxy(World world, long id, int x, int y) {
		super(world);
		this.id = id;
		this.x = x;
		this.y = y;
	}

	@Override
	public TeamMember findIfNotFound() {
		Chunk chunk = getWorld().getMap().chunkAt(x, y);
		if (chunk != null) {
			StructureEntity structure = chunk.tileAt(x, y).getStructure();
			if (structure != null && structure.getId() == id) {
				return structure;
			}
		}
		return this;
	}
}
