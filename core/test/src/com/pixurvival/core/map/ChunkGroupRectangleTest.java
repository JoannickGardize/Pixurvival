package com.pixurvival.core.map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.pixurvival.core.map.chunk.ChunkGroupRectangle;
import com.pixurvival.core.util.Vector2;

public class ChunkGroupRectangleTest {

	@Test
	public void setTest() {

		ChunkGroupRectangle rectangle = new ChunkGroupRectangle();
		rectangle.set(new Vector2(16, 16), 40);

		Assertions.assertEquals(-1, rectangle.getXStart());
		Assertions.assertEquals(1, rectangle.getXEnd());
		Assertions.assertEquals(-1, rectangle.getYStart());
		Assertions.assertEquals(1, rectangle.getYEnd());

		Assertions.assertFalse(rectangle.set(new Vector2(20, 12), 40));

		Assertions.assertTrue(rectangle.set(new Vector2(48, 48), 40));

		Assertions.assertEquals(0, rectangle.getXStart());
		Assertions.assertEquals(2, rectangle.getXEnd());
		Assertions.assertEquals(0, rectangle.getYStart());
		Assertions.assertEquals(2, rectangle.getYEnd());
	}
}
