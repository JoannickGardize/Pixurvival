package com.pixurvival.client;

import java.util.Collection;

import com.pixurvival.core.EndGameData;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.serialization.ContentPackValidityCheckResult;
import com.pixurvival.core.message.LoginResponse;
import com.pixurvival.core.message.lobby.LobbyMessage;

public interface ClientGameListener {

	void loginResponse(LoginResponse response);

	void initializeGame();

	void error(Throwable e);

	void playerFocusChanged();

	void gameStarted();

	void gameEnded(EndGameData data);

	void enterLobby();

	void lobbyMessageReceived(LobbyMessage message);

	void contentPackAvailable(ContentPackIdentifier identifier);

	void questionDownloadContentPack(ContentPackIdentifier identifier, ContentPackValidityCheckResult checkResult);

	void discovered(Collection<ItemCraft> itemCrafts);
}
