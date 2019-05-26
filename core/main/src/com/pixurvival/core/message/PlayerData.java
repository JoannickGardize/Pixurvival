package com.pixurvival.core.message;

import com.pixurvival.core.livingEntity.Equipment;

import lombok.Data;

@Data
public class PlayerData {

	private long id;
	private String name;
	private float strength;
	private float agility;
	private float intelligence;
	private Equipment equipment;

	// TODO Custom Serializer for performance
}
