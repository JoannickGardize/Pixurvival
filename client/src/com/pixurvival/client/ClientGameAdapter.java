package com.pixurvival.client;

import com.pixurvival.core.EndGameData;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.lobby.LobbyList;

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
	public void lobbyListReceived(LobbyList list) {
		// TODO Auto-generated method stub

	}

}
