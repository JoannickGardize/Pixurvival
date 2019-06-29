package com.pixurvival.core.contentPack.gameMode;

import java.io.Serializable;

import com.pixurvival.core.time.DayCycleRun;

public abstract class DayCycle implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract DayCycleRun create();
}
