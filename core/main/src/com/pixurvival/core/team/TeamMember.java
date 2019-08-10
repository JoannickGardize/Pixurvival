package com.pixurvival.core.team;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.util.Vector2;

public interface TeamMember extends Positionnable {

	Team getTeam();

	StatSet getStats();

	Vector2 getTargetPosition();

	TeamMember getOrigin();

}
