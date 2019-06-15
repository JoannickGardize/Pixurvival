package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.livingEntity.PlayerInventory;

import lombok.Data;

@Data
public class CreateWorld {

	private long id;
	private ContentPackIdentifier contentPackIdentifier;
	private int gameModeId;
	private long myPlayerId;
	private PlayerInventory inventory;
	private String[] teamNames;
	private int myTeamId;
}
