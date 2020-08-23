package com.pixurvival.core.message.lobby;

import com.pixurvival.core.contentPack.ContentPackIdentifier;

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
	private ContentPackIdentifier[] availableContentPacks;
	private int selectedContentPackIndex;
	private int selectedGameModeIndex;
	private int maxPlayer;

	/**
	 * modCount to check if the teams structures is the the as the one that the
	 * client wanted to be ready for.
	 */
	private int modCount;
}
