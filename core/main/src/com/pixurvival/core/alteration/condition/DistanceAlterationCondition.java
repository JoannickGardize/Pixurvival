package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.team.TeamMember;

public class DistanceAlterationCondition implements AlterationCondition {

    private TargetType targetType;

    @Override
    public boolean test(TeamMember entity) {

        return false;
    }
}