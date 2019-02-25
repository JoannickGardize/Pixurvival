package com.pixurvival.core.message;

import com.pixurvival.core.aliveEntity.PlayerEntity;

public interface IPlayerActionRequest {

	void apply(PlayerEntity player);
}
