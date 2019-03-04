package com.pixurvival.core.message.request;

import com.pixurvival.core.aliveEntity.PlayerEntity;

public interface IPlayerActionRequest {

	void apply(PlayerEntity player);

	boolean isClientPreapply();
}
