package com.pixurvival.core.contentPack.gameMode;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.NoEndCondition;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.gameMode.spawn.AutoSquarePlayerSpawn;
import com.pixurvival.core.contentPack.gameMode.spawn.PlayerSpawn;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMode extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	@Valid
	private IntegerInterval teamNumberInterval = new IntegerInterval();

	@Valid
	private IntegerInterval teamSizeInterval = new IntegerInterval();

	@Required
	@ElementReference
	private MapProvider mapProvider;

	@Required
	@ElementReference
	private Ecosystem ecosystem;

	private DayCycle dayCycle = new EternalDayCycle();

	@Valid
	private EndGameCondition endGameCondition = new NoEndCondition();

	private List<Event> events = new ArrayList<>();

	private MapLimits mapLimits;

	private PlayerSpawn playerSpawn = new AutoSquarePlayerSpawn();
}
