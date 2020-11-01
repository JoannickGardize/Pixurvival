package com.pixurvival.core.contentPack.gameMode.role;

import java.io.Serializable;

import com.pixurvival.core.livingEntity.PlayerEntity;

public interface WinCondition extends Serializable {

	public boolean test(PlayerEntity playerEntity);
}
