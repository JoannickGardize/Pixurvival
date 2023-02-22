package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.time.DayCycleRun;

import java.io.Serializable;

public abstract class DayCycle implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract DayCycleRun create();
}
