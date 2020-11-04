package com.pixurvival.core;

import com.pixurvival.core.livingEntity.PlayerEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerEndGameData {

	private long playerId;
	private int roleId;

	public PlayerEndGameData(PlayerEntity player) {
		playerId = player.getId();
		roleId = player.getRole() == null ? -1 : player.getRole().getId();
	}
}
