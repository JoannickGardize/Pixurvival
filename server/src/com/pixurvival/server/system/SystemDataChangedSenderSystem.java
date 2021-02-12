package com.pixurvival.server.system;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.Inject;
import com.pixurvival.core.system.SystemData;
import com.pixurvival.core.system.interest.SystemDataChangedInterest;
import com.pixurvival.server.PlayerGameSession;

import lombok.Setter;

@Setter
public class SystemDataChangedSenderSystem implements GameSystem, SystemDataChangedInterest {

	@Inject
	private List<PlayerGameSession> players = new ArrayList<>();

	@Override
	public void dataChanged(SystemData data) {
		players.forEach(p -> p.getConnection().sendTCP(data));
	}
}
