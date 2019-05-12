package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.mapBounds.MapBounds;
import com.pixurvival.core.contentPack.gameMode.mapBounds.NoMapBounds;
import com.pixurvival.core.contentPack.gameMode.spawnStrategy.SpawnStrategy;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

public class GameMode extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@Valid
	private IntegerInterval numPlayerInterval = new IntegerInterval();

	@Valid
	private IntegerInterval teamSizeInterval = new IntegerInterval();

	@Required
	@ElementReference
	private MapGenerator mapGenerator;

	@Required
	@ElementReference
	private Ecosystem ecosystem;

	@Valid
	private EndGameCondition endGameCondition;

	@Valid
	private MapBounds mapBounds = new NoMapBounds();

	@Valid
	private SpawnStrategy spawnStrategy;
}
