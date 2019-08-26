package com.pixurvival.server;

import com.pixurvival.core.message.WorldUpdate;

public interface NetworkListener {
	void sent(WorldUpdate worldUpdate);
}
