package com.pixurvival.core.message;

import com.pixurvival.core.livingEntity.PlayerInventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitializeGame {

	private long myPlayerId;
	private PlayerInventory inventory;
	private PlayerData[] playerData;
}
