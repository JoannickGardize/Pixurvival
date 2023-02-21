package com.pixurvival.core.alteration.condition;

import java.io.Serializable;

import com.pixurvival.core.team.TeamMember;

public interface AlterationCondition extends Serializable {

	boolean test(TeamMember entity);
}
