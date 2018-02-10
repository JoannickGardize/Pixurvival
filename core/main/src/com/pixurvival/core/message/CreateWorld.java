package com.pixurvival.core.message;

import lombok.Data;

@Data
public class CreateWorld {

	private long id;
	private int mapWidth;
	private int mapHeight;
}
