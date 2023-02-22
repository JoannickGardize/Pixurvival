package com.pixurvival.core.system.mapLimits;

import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.SystemData;
import com.pixurvival.core.util.Rectangle;
import lombok.Data;

@Data
public class MapLimitsSystemData implements SystemData {

    private Rectangle rectangle;
    private float trueDamagePerSecond;
    private MapLimitsAnchorRun from;
    private MapLimitsAnchorRun to;

    @Override
    public Class<? extends GameSystem> systemOwnerType() {
        return MapLimitsSystem.class;
    }
}
