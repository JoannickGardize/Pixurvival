package com.pixurvival.core.map.analytics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class BooleanExtensibleGridTest {

	@Test
	public void getAndSetTest() {
		BooleanExtensibleGrid grid = new BooleanExtensibleGrid();

		Assert.assertEquals(0, grid.size());
		Assert.assertFalse(grid.get(1, 1));
		Assert.assertFalse(grid.get(-1, -1));
		Assert.assertEquals(0, grid.size());

		grid.set(0, 0, true);
		grid.set(0, 0, true);
		grid.set(3, 5, true);
		grid.set(-1, -2, true);
		grid.set(30, 0, true);

		Assert.assertEquals(4, grid.size());
		Assert.assertTrue(grid.get(0, 0));
		Assert.assertTrue(grid.get(3, 5));
		Assert.assertTrue(grid.get(-1, -2));
		Assert.assertTrue(grid.get(30, 0));
		Assert.assertFalse(grid.get(1, 0));

		grid.set(3, 5, false);
		grid.set(3, 5, false);
		Assert.assertEquals(3, grid.size());
		Assert.assertFalse(grid.get(3, 5));
	}

	@Test
	public void getExternalPointsTest() throws IOException {
		BooleanExtensibleGrid grid = new BooleanExtensibleGrid();
		readPointsCloud("points_cloud.txt", (x, y) -> grid.set(x, y, true));

		List<Position> expectedResultList = new ArrayList<>();
		readPointsCloud("external_points.txt", (x, y) -> expectedResultList.add(new Position(x, y)));
		Position[] expectedResult = expectedResultList.toArray(new Position[expectedResultList.size()]);

		Position[] actualResult = grid.getExternalPoints();

		Assert.assertEquals(expectedResult.length, actualResult.length);
	}

	private static void printPoints(Position[] positions) {
		List<List<Integer>> points = new ArrayList<>();
		for (Position position : positions) {
			while (position.getY() >= points.size()) {
				points.add(new ArrayList<>());
			}
			List<Integer> xList = points.get(position.getY());
			while (position.getX() >= xList.size()) {
				xList.add(0);
			}
			xList.set(position.getX(), xList.get(position.getX()) + 1);
		}
		for (List<Integer> xList : points) {
			for (int i : xList) {
				if (i == 0) {
					System.out.print(' ');
				} else {
					System.out.print(i);
				}
			}
			System.out.println();
		}
	}

	private static void readPointsCloud(String fileName, PositionConsumer action) throws IOException {
		InputStream stream = BooleanExtensibleGridTest.class.getClassLoader().getResourceAsStream(fileName);
		int character;
		int x = 0;
		int y = 0;
		while ((character = stream.read()) != -1) {
			if (character == ' ') {
				x++;
			} else if (character == '1') {
				action.accept(x, y);
				x++;
			} else if (character == '\n') {
				y++;
				x = 0;
			}
		}
	}
}
