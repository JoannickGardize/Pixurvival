package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.time.DayCycleRun;
import com.pixurvival.core.time.EternalDayCycleRun;

public class EternalDayCycle extends DayCycle {

	private static final long serialVersionUID = 1L;

	@Override
	public DayCycleRun create() {
		return new EternalDayCycleRun();
	}

}
