package com.pixurvival.core.map.analytics;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.pixurvival.core.util.Vector2;

public class PositionTest {

	@Test
	public void searchPositionInSquareTest() {
		Position position = new Position(3, 3);
		Set<Position> expectedSet = new HashSet<>();
		expectedSet.add(new Position(5, 1));
		expectedSet.add(new Position(5, 2));
		expectedSet.add(new Position(5, 3));
		expectedSet.add(new Position(5, 4));
		expectedSet.add(new Position(5, 5));
		expectedSet.add(new Position(4, 5));
		expectedSet.add(new Position(3, 5));
		expectedSet.add(new Position(2, 5));
		expectedSet.add(new Position(1, 5));
		expectedSet.add(new Position(1, 4));
		expectedSet.add(new Position(1, 3));
		expectedSet.add(new Position(1, 2));
		expectedSet.add(new Position(1, 1));
		expectedSet.add(new Position(2, 1));
		expectedSet.add(new Position(3, 1));
		expectedSet.add(new Position(4, 1));

		Set<Position> actualSet = new HashSet<>();

		position.searchPositionInSquare(2, (x, y) -> {
			Position pos = new Position(x, y);
			if (!actualSet.add(pos)) {
				Assert.fail("Two times same point : " + pos);
			}
			return false;
		});

		Assert.assertEquals(expectedSet, actualSet);
	}

	@Test
	public void getXGroupTest() {
		Assert.assertEquals(-1, new Position(-5, 2).getXGroup(16));
		Assert.assertEquals(0, new Position(0, 0).getXGroup(16));
		Assert.assertEquals(1, new Position(16, 0).getXGroup(16));
		Assert.assertEquals(-1, new Position(-1, -2).getXGroup(16));
		Assert.assertEquals(-1, new Position(-16, 2).getXGroup(16));
		Assert.assertEquals(-2, new Position(-17, 2).getXGroup(16));
		Assert.assertEquals(-2, new Position(-32, 2).getXGroup(16));
		Assert.assertEquals(-3, new Position(-33, 2).getXGroup(16));
	}

	@Test
	public void getYGroupTest() {
		Assert.assertEquals(-1, new Position(1, -5).getYGroup(16));
		Assert.assertEquals(0, new Position(0, 0).getYGroup(16));
		Assert.assertEquals(-1, new Position(30, -16).getYGroup(16));
		Assert.assertEquals(-2, new Position(-30, -17).getYGroup(16));
	}

	@Test
	public void toVector2Test() {
		Position position = new Position(-1, -1);

		Assert.assertTrue("expected : " + new Vector2(-15.5f, -15.5f) + ", actual : " + position.toVector2(16), new Vector2(-15.5f, -15.5f).epsilonEquals(position.toVector2(16), 0.00001f));
	}

}
