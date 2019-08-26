package com.pixurvival.core.contentPack.gameMode;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.NoEndCondition;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.map.MapGenerator;
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
	private MapGenerator mapGenerator;

	@Required
	@ElementReference
	private Ecosystem ecosystem;

	private DayCycle dayCycle = new EternalDayCycle();

	@Valid
	private EndGameCondition endGameCondition = new NoEndCondition();

	private List<Event> events = new ArrayList<>();

	private double spawnSquareSize = 490;

	private boolean mapLimitEnabled = false;

	private double mapLimitSize = 500;

	private float mapLimitDamagePerSecond = 5;

}
