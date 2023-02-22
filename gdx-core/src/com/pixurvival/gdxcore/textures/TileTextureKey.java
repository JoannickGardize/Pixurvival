package com.pixurvival.gdxcore.textures;

import com.pixurvival.core.contentPack.map.Tile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class TileTextureKey {
    private Tile middle;
    private Tile topLeft;
    private Tile topRight;
    private Tile bottomLeft;
    private Tile bottomRight;

    public TileTextureKey(TileTextureKey other) {
        middle = other.middle;
        topLeft = other.topLeft;
        topRight = other.topRight;
        bottomLeft = other.bottomLeft;
        bottomRight = other.bottomRight;
    }

    public void setAll(Tile tile) {
        middle = tile;
        topLeft = tile;
        topRight = tile;
        bottomLeft = tile;
        bottomRight = tile;
    }

    public static TileTextureKey ofAll(Tile tile) {
        TileTextureKey key = new TileTextureKey();
        key.setAll(tile);
        return key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + middle.getId();
        result = prime * result + topLeft.getId();
        result = prime * result + topRight.getId();
        result = prime * result + bottomLeft.getId();
        result = prime * result + bottomRight.getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TileTextureKey)) {
            return false;
        }
        TileTextureKey other = (TileTextureKey) obj;
        return middle == other.middle && topLeft == other.topLeft && topRight == other.topRight && bottomLeft == other.bottomLeft && bottomRight == other.bottomRight;
    }
}
