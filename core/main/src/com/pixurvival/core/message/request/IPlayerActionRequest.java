package com.pixurvival.core.message.request;

import com.pixurvival.core.livingEntity.PlayerEntity;

public interface IPlayerActionRequest {

	void apply(PlayerEntity player);

	boolean isClientPreapply();
}
