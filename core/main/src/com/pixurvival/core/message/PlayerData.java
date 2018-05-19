package com.pixurvival.core.message;

import com.pixurvival.core.aliveEntity.Equipment;

import lombok.Data;

@Data
public class PlayerData {

	private long id;
	private String name;
	private float strength;
	private float agility;
	private float intelligence;
	private Equipment equipment;
}
