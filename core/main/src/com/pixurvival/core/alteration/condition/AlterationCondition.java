package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.team.TeamMember;

import java.io.Serializable;

public interface AlterationCondition extends Serializable {

    boolean test(TeamMember entity);
}
