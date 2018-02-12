package com.pixurvival.core.message;

import com.pixurvival.core.contentPack.ContentPackIdentifier;

import lombok.Data;

@Data
public class CreateWorld {

	private long id;
	private int mapWidth;
	private int mapHeight;
	private ContentPackIdentifier contentPackIdentifier;
}
