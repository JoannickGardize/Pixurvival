package com.pixurvival.core.message.lobby;

import com.pixurvival.core.contentPack.summary.ContentPackSummary;

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
	private ContentPackSummary[] availableContentPacks;
	private int selectedContentPackIndex;
	private int selectedGameModeIndex;

	/**
	 * modCount to check if the teams structures is the the as the one that the
	 * client wanted to be ready for.
	 */
	private int modCount;
}
