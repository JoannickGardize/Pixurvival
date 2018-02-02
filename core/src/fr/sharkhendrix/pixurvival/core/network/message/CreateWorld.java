package fr.sharkhendrix.pixurvival.core.network.message;

import lombok.Data;

@Data
public class CreateWorld {

	private long id;
	private int mapWidth;
	private int mapHeight;
}
