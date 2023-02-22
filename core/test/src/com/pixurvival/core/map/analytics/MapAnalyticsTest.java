package com.pixurvival.core.map.analytics;

import com.pixurvival.core.map.TiledMapCursorMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapAnalyticsTest {

    @Test
    public void isConnectedTest() {
        TiledMapCursorMock map = new TiledMapCursorMock();
        // map.addSolidTile(1, 0);

        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(0, 0), 0));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(10, 0), 0));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(0, 10), 0));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(-10, 0), 0));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(0, -10), 0));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(10, 10), 0));

        map.addSolidTile(0, 0);

        Assertions.assertFalse(MapAnalytics.isConnected(map, new Position(0, 0), new Position(1, 0), 0));
        Assertions.assertFalse(MapAnalytics.isConnected(map, new Position(1, 0), new Position(0, 0), 0));
        Assertions.assertFalse(MapAnalytics.isConnected(map, new Position(-1, 0), new Position(1, 0), 0));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(-1, 0), new Position(1, 0), 2));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(1, 0), new Position(-1, 0), 2));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 1), new Position(0, -1), 2));
        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, -1), new Position(0, 1), 2));

        map.addSolidTile(1, 2);
        map.addSolidTile(-1, 2);
        map.addSolidTile(0, 4);

        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, -1), new Position(0, 7), 3));

        map.reset();

        map.addSolidTile(-2, 1);
        map.addSolidTile(-1, 1);
        map.addSolidTile(0, 1);
        map.addSolidTile(1, 1);
        map.addSolidTile(2, 1);

        Assertions.assertTrue(MapAnalytics.isConnected(map, new Position(0, 0), new Position(0, 7), 3));
    }
}
