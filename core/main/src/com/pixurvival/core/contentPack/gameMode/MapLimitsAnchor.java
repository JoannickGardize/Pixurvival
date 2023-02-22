package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapLimitsAnchor {

    @Positive
    private long time = 0;

    @Positive
    private float size = 500;

    @Positive
    private float damagePerSecond = 10;
}
