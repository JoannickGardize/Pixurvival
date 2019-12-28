package com.pixurvival.client;

import com.pixurvival.core.EndGameData;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.lobby.LobbyMessage;

public interface ClientGameListener {

	void loginResponse(LoginResponse response);

	void initializeGame();

	void error(Throwable e);

	void spectatorStarted();

	void gameEnded(EndGameData data);

	void enterLobby();

	void lobbyMessageReceived(LobbyMessage message);

	void contentPackAvailable(ContentPackIdentifier identifier);

	void questionDownloadContentPack(ContentPackIdentifier identifier, ContentPackValidityCheckResult checkResult);
}
