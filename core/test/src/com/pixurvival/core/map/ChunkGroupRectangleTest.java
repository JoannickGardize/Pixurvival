package com.pixurvival.core.map;

import org.junit.Assert;
import org.junit.Test;

import com.pixurvival.core.util.Vector2;

public class ChunkGroupRectangleTest {

	@Test
	public void setTest() {

		ChunkGroupRectangle rectangle = new ChunkGroupRectangle();
		rectangle.set(new Vector2(16, 16), 40);

		Assert.assertEquals(-1, rectangle.getXStart());
		Assert.assertEquals(1, rectangle.getXEnd());
		Assert.assertEquals(-1, rectangle.getYStart());
		Assert.assertEquals(1, rectangle.getYEnd());

		Assert.assertFalse(rectangle.set(new Vector2(20, 12), 40));

		Assert.assertTrue(rectangle.set(new Vector2(48, 48), 40));

		Assert.assertEquals(0, rectangle.getXStart());
		Assert.assertEquals(2, rectangle.getXEnd());
		Assert.assertEquals(0, rectangle.getYStart());
		Assert.assertEquals(2, rectangle.getYEnd());
	}
}
