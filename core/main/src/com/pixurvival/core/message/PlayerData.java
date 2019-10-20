package com.pixurvival.core.message;

import com.pixurvival.core.livingEntity.Equipment;

import lombok.Data;

@Data
public class PlayerData {

	private long id;
	private String name;
	private Equipment equipment;

	// TODO Custom Serializer for performance
}
