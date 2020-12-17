package com.pixurvival.core.contentPack.gameMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapLimits implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean shrinkRandomly = false;

	@Positive
	private float initialSize = 500;

	@Positive
	private float initialDamagePerSecond = 10;

	@Valid
	private List<MapLimitsAnchor> anchors = new ArrayList<>();
}
