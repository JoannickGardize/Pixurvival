package fr.sharkhendrix.pixurvival.core.network.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StartGame {

	private CreateWorld createWorld;
	private long myPlayerId;
}
