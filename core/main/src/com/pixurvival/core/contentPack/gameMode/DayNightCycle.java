package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.time.DayCycleRun;
import com.pixurvival.core.time.DayNightCycleRun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayNightCycle extends DayCycle {

	private static final long serialVersionUID = 1L;

	private long dayDuration;
	private long nightDuration;

	@Override
	public DayCycleRun create() {
		return new DayNightCycleRun(dayDuration, nightDuration);
	}
}
