package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.FloatComparison;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatAlterationCondition implements AlterationCondition {

    private StatType statType = StatType.MAX_HEALTH;

    private FloatComparison operator = FloatComparison.GREATER_THAN;

    private float value;

    @Override
    public boolean test(TeamMember entity) {
        return operator.test(entity.getStats().getValue(statType), value);
    }
}
