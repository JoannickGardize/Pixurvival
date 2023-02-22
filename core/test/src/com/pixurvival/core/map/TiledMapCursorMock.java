package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.map.analytics.Position;
import com.pixurvival.core.map.analytics.TiledMapCursor;

import java.util.HashSet;
import java.util.Set;

public class TiledMapCursorMock extends TiledMapCursor {

    private static MapTile emptyTile = new EmptyTile(new Tile());

    private static MapTile solidTile = new EmptyTile(solideTileDefinition());

    private Set<Position> solidTiles = new HashSet<>();

    private Position tmpPosition = new Position();

    public TiledMapCursorMock() {
        super(null);
    }

    private static Tile solideTileDefinition() {
        Tile tile = new Tile();
        tile.setSolid(true);
        return tile;
    }

    public void reset() {
        solidTiles.clear();
    }

    public void addSolidTile(int x, int y) {
        solidTiles.add(new Position(x, y));
    }

    @Override
    public MapTile tileAt(Position tilePosition) {
        return tileAt(tilePosition.getX(), tilePosition.getY());
    }

    @Override
    public MapTile tileAt(int x, int y) {
        tmpPosition.set(x, y);
        return solidTiles.contains(tmpPosition) ? solidTile : emptyTile;
    }
}
