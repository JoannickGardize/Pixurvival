package com.pixurvival.core.util;

import org.junit.Assert;
import org.junit.Test;

public class ByteArray2DTest {

	@Test
	public void set() {
		ByteArray2D a = new ByteArray2D(8, 6);

		a.set(4, 3, (byte) 5);
		Assert.assertEquals(8, a.getWidth());
		Assert.assertEquals(6, a.getHeight());
		Assert.assertEquals(5, a.get(4, 3));
	}

	@Test
	public void fill() {
		ByteArray2D a = new ByteArray2D(8, 6);

		a.fill((byte) 7);
		Assert.assertEquals(8, a.getWidth());
		Assert.assertEquals(6, a.getHeight());
		Assert.assertEquals(7, a.get(4, 3));
		Assert.assertEquals(7, a.get(0, 0));
	}

	@Test
	public void setRect() {
		ByteArray2D a = new ByteArray2D(8, 8);
		a.fill((byte) 1);
		ByteArray2D b = new ByteArray2D(4, 4);
		b.fill((byte) 2);
		a.setRect(2, 2, b);
		Assert.assertEquals(2, a.get(2, 2));
		Assert.assertEquals(2, a.get(5, 2));
		Assert.assertEquals(2, a.get(2, 5));
		Assert.assertEquals(2, a.get(5, 5));
		Assert.assertEquals(1, a.get(6, 6));
		Assert.assertEquals(1, a.get(0, 0));
		Assert.assertEquals(1, a.get(1, 3));
		Assert.assertEquals(1, a.get(4, 1));
	}

	@Test
	public void getRect() {
		ByteArray2D a = new ByteArray2D(8, 8);
		a.fill((byte) 1);
		ByteArray2D b = new ByteArray2D(4, 4);
		b.fill((byte) 2);
		a.setRect(2, 2, b);
		Assert.assertEquals(b, a.getRect(2, 2, 4, 4));
	}
}
