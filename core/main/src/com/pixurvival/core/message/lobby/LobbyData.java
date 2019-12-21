package com.pixurvival.core.message.lobby;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author SharkHendrix
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class LobbyData extends LobbyMessage {
	private String myTeamName;
	private LobbyPlayer myPlayer;
	private LobbyTeam[] players;

	/**
	 * modCount to check if the teams structures is the the as the one that the
	 * client wanted to be ready for.
	 */
	private int modCount;
}
