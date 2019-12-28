package com.pixurvival.client;

import com.pixurvival.core.EndGameData;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.lobby.LobbyMessage;

public class ClientGameAdapter implements ClientGameListener {

	@Override
	public void loginResponse(LoginResponse response) {
	}

	@Override
	public void initializeGame() {
	}

	@Override
	public void error(Throwable e) {
	}

	@Override
	public void spectatorStarted() {
	}

	@Override
	public void gameEnded(EndGameData data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterLobby() {
	}

	@Override
	public void lobbyMessageReceived(LobbyMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contentPackAvailable(ContentPackIdentifier identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void questionDownloadContentPack(ContentPackIdentifier identifier, ContentPackValidityCheckResult checkResult) {
	}

}
