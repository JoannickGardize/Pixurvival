package com.pixurvival.client;

import com.pixurvival.core.message.LoginResponse;

public interface ClientGameListener {

	void loginResponse(LoginResponse response);

	void initializeGame();

	void error(Throwable e);

	void spectatorStarted();
}
