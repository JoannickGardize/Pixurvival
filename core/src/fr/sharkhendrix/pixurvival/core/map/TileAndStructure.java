package fr.sharkhendrix.pixurvival.core.map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TileAndStructure implements Tile {

	private EmptyTile tile;
	private Structure structure;

	@Override
	public boolean isSolid() {
		return structure.isSolid();
	}

	@Override
	public double getVelocityFactor() {
		return tile.getVelocityFactor();
	}

}
