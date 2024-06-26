package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class TileGenerator implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<HeightmapCondition> heightmapConditions = new ArrayList<>();

    @Valid
    private TileHashmap tileHashmap = new TileHashmap();

    public boolean test(int x, int y, float[] heightmapRun) {
        for (HeightmapCondition h : heightmapConditions) {
            if (!h.test(x, y, heightmapRun)) {
                return false;
            }
        }
        return true;
    }

    public Tile getTileAt(int x, int y, float[] heightmapRun) {
        return tileHashmap.get(x, y, heightmapRun);
    }
}
