package com.pixurvival.core.message;

import com.pixurvival.core.livingEntity.Equipment;

import lombok.Data;

@Data
public class PlayerData {

	private long id;
	private Equipment equipment;

	// TODO Custom Serializer for performance
}
