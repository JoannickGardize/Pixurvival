package fr.sharkhendrix.pixurvival.client;

import fr.sharkhendrix.pixurvival.core.message.LoginResponse;

public interface ClientGameListener {

	void loginResponse(LoginResponse response);

	void startGame();
}
