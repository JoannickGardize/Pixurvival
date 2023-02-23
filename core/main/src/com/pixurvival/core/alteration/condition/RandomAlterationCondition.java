package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.team.TeamMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RandomAlterationCondition implements AlterationCondition {

    private float percentChance;
    
    @Override
    public boolean test(TeamMember entity) {
        return entity.getWorld().getRandom().nextFloat() < percentChance;
    }
}
