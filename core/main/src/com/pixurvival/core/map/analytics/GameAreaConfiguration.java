package com.pixurvival.core.map.analytics;

import com.pixurvival.core.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GameAreaConfiguration {

    private Area area;

    private Vector2[] spawnSpots;

}
