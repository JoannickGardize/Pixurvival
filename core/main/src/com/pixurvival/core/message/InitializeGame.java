package com.pixurvival.core.message;

import com.pixurvival.core.aliveEntity.PlayerInventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitializeGame {

	private CreateWorld createWorld;
	private long myPlayerId;
	private PlayerInventory inventory;
	private PlayerData[] playerData;
}
