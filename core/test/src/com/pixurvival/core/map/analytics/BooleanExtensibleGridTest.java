package com.pixurvival.core.map.analytics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BooleanExtensibleGridTest {

    @Test
    public void getAndSetTest() {
        BooleanExtensibleGrid grid = new BooleanExtensibleGrid();

        Assertions.assertEquals(0, grid.size());
        Assertions.assertFalse(grid.get(1, 1));
        Assertions.assertFalse(grid.get(-1, -1));
        Assertions.assertEquals(0, grid.size());

        grid.set(0, 0, true);
        grid.set(0, 0, true);
        grid.set(3, 5, true);
        grid.set(-1, -2, true);
        grid.set(30, 0, true);

        Assertions.assertEquals(4, grid.size());
        Assertions.assertTrue(grid.get(0, 0));
        Assertions.assertTrue(grid.get(3, 5));
        Assertions.assertTrue(grid.get(-1, -2));
        Assertions.assertTrue(grid.get(30, 0));
        Assertions.assertFalse(grid.get(1, 0));

        grid.set(3, 5, false);
        grid.set(3, 5, false);
        Assertions.assertEquals(3, grid.size());
        Assertions.assertFalse(grid.get(3, 5));
    }

    @Test
    public void getExternalPointsTest() throws IOException {
        BooleanExtensibleGrid grid = new BooleanExtensibleGrid();
        readPointsCloud("points_cloud.txt", (x, y) -> grid.set(x, y, true));

        List<Position> expectedResultList = new ArrayList<>();
        readPointsCloud("external_points.txt", (x, y) -> expectedResultList.add(new Position(x, y)));
        Position[] expectedResult = expectedResultList.toArray(new Position[expectedResultList.size()]);

        Position[] actualResult = grid.getExternalPoints();

        Assertions.assertEquals(expectedResult.length, actualResult.length);
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
