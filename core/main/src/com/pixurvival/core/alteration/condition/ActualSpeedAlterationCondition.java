package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.FloatComparison;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualSpeedAlterationCondition implements AlterationCondition {

    private FloatComparison operator = FloatComparison.GREATER_THAN;

    private float value;

    @Override
    public boolean test(TeamMember entity) {
        if (!(entity instanceof Entity)) {
            return value == 0;
        }
        return operator.test(((Entity) entity).getVelocity().lengthSquared(), value * value);
    }
}
