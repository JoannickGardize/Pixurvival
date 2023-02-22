package com.pixurvival.core.contentPack.gameMode.role;

import com.pixurvival.core.livingEntity.PlayerEntity;

import java.io.Serializable;

public interface WinCondition extends Serializable {

    public boolean test(PlayerEntity playerEntity);
}
