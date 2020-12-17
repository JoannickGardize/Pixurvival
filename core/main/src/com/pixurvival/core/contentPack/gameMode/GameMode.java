package com.pixurvival.core.contentPack.gameMode;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.IntegerInterval;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.gameMode.endGameCondition.EndGameCondition;
import com.pixurvival.core.contentPack.gameMode.event.Event;
import com.pixurvival.core.contentPack.gameMode.role.Roles;
import com.pixurvival.core.contentPack.gameMode.spawn.AutoSquarePlayerSpawn;
import com.pixurvival.core.contentPack.gameMode.spawn.PlayerSpawn;
import com.pixurvival.core.contentPack.map.MapProvider;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
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

	@ElementReference
	private MapProvider mapProvider;

	@ElementReference
	private Ecosystem ecosystem;

	@Valid
	private DayCycle dayCycle = new EternalDayCycle();

	/**
	 * No roles if null
	 */
	@Valid
	@Nullable
	private Roles roles;

	@Valid
	@Length(min = 1)
	private List<EndGameCondition> endGameConditions = new ArrayList<>();

	@Valid
	private List<Event> events = new ArrayList<>();

	/**
	 * No map limits if null
	 */
	@Nullable
	@Valid
	private MapLimits mapLimits;

	@Valid
	private PlayerSpawn playerSpawn = new AutoSquarePlayerSpawn();

	public boolean updateEndGameConditions(World world) {
		for (EndGameCondition condition : endGameConditions) {
			if (condition.update(world)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize() {
		for (int i = 0; i < endGameConditions.size(); i++) {
			endGameConditions.get(i).setId(i);
		}
	}
}
