package com.pixurvival.core.map;

import org.junit.Assert;
import org.junit.Test;

import com.pixurvival.core.map.ChunkPosition.NeighbourType;

public class ChunkPositionTest {

	@Test
	public void neighbourTypeOfTest() {

		ChunkPosition position = new ChunkPosition(10, -10);

		ChunkPosition neighbour = new ChunkPosition(10, -11);
		Assert.assertEquals(NeighbourType.SOUTH, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(10, -9);
		Assert.assertEquals(NeighbourType.NORTH, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(11, -10);
		Assert.assertEquals(NeighbourType.EAST, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(9, -10);
		Assert.assertEquals(NeighbourType.WEST, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(11, -11);
		Assert.assertEquals(NeighbourType.SOUTH_EAST, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(11, -9);
		Assert.assertEquals(NeighbourType.NORTH_EAST, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(9, -11);
		Assert.assertEquals(NeighbourType.SOUTH_WEST, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(9, -9);
		Assert.assertEquals(NeighbourType.NORTH_WEST, position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(10, -10);
		Assert.assertNull(position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(12, -10);
		Assert.assertNull(position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(10, -12);
		Assert.assertNull(position.neighbourTypeOf(neighbour));

		neighbour = new ChunkPosition(13, -12);
		Assert.assertNull(position.neighbourTypeOf(neighbour));
	}
}
