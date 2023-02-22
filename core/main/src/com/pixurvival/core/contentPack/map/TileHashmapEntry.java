package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.contentPack.FloatHolder;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TileHashmapEntry implements Serializable, FloatHolder {

    private static final long serialVersionUID = 1L;

    @ElementReference
    private Tile tile;

    @Bounds(min = 0, max = 1, maxInclusive = true)
    private float next;

    @Override
    public float getFloatValue() {
        return next;
    }
}
