package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.validation.annotation.Ascending;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TileHashmap implements Serializable {

    private static final long serialVersionUID = 1L;

    @ElementReference("<<<.heightmaps")
    private Heightmap heightmap;

    @Valid
    @Length(min = 1)
    @Ascending(lastValue = 1)
    private List<TileHashmapEntry> entries = new ArrayList<>();

    public Tile get(int x, int y, float[] run) {
        float noise = heightmap.getNoise(x, y, run);
        for (TileHashmapEntry entry : entries) {
            if (noise < entry.getNext()) {
                return entry.getTile();
            }
        }
        return entries.get(entries.size() - 1).getTile();
    }
}
