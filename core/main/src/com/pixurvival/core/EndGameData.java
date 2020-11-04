package com.pixurvival.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndGameData {

	private long time;
	private PlayerEndGameData[] playerWonIds;
	private PlayerEndGameData[] playerLostIds;
}
