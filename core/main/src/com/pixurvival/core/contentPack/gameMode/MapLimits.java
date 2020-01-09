package com.pixurvival.core.contentPack.gameMode;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapLimits {

	private boolean shrinkRandomly = false;

	private float initialSize = 500;

	private float initialDamagePerSecond = 10;

	private List<MapLimitsAnchor> anchors = new ArrayList<>();
}
