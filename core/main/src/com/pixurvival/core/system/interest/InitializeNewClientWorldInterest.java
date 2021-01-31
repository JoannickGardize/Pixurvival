package com.pixurvival.core.system.interest;

import com.pixurvival.core.message.CreateWorld;

public interface InitializeNewClientWorldInterest extends Interest {

	void initializeNewClientWorld(CreateWorld createWorld);
}
