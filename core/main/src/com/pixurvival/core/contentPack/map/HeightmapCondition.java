package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import lombok.Data;

import java.io.Serializable;

@Data
public class HeightmapCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @ElementReference("<<<<.heightmaps")
    private Heightmap heightmap;

    @Bounds(min = 0, max = 1, maxInclusive = true)
    private float min;

    @Bounds(min = 0, max = 1, maxInclusive = true)
    private float max;

    public boolean test(int x, int y) {
        float noise = heightmap.getNoise(x, y);
        return noise >= min && noise < max;
    }
}
