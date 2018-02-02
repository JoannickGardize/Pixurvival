package fr.sharkhendrix.pixurvival.client;

import fr.sharkhendrix.pixurvival.core.network.message.LoginResponse;

public interface GameListener {

	void loginResponse(LoginResponse response);

}
