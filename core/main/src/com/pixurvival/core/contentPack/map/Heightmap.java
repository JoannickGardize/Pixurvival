package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.map.generator.SimplexNoise;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Heightmap extends NamedIdentifiedElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Bounds(min = 1)
    private int numberOfoctaves = 1;

    @Bounds(min = 0)
    private float persistence;

    @Bounds(min = 0)
    private float scale;

    private transient SimplexNoise simplexNoise;

    public void initialize(long seed) {
        simplexNoise = new SimplexNoise(numberOfoctaves, persistence, scale, seed);
    }

    public float getNoise(int x, int y, float[] run) {
        float result = run[getId()];
        if (result == -1) {
            result = simplexNoise.getNoise(x, y);
            run[getId()] = result;
        }
        return result;
    }
}
