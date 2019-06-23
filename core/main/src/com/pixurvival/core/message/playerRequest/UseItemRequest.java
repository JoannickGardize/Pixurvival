package com.pixurvival.core.message.playerRequest;

import com.pixurvival.core.livingEntity.PlayerEntity;

public class UseItemRequest implements IPlayerActionRequest {

	private int id;

	@Override
	public void apply(PlayerEntity player) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClientPreapply() {
		// TODO Auto-generated method stub
		return false;
	}

}
