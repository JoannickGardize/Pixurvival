package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.EntitySearchUtils;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.FloatComparison;
import com.pixurvival.core.util.IntWrapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityAroundCountAlterationCondition implements AlterationCondition {

    private TargetType targetType = TargetType.ALL_OTHERS;

    @Valid
    private ElementSet<Creature> creatureFilter = new AllElementSet<>();

    @Positive
    private float distance;

    private FloatComparison operator = FloatComparison.GREATER_THAN;

    @Positive
    private int count;

    @Override
    public boolean test(TeamMember entity) {
        IntWrapper counter = new IntWrapper();
        EntitySearchUtils.forEach(entity, targetType, distance, other -> {
            if (entity.distanceSquared(other) <= distance * distance
                    && (!(other instanceof CreatureEntity) || creatureFilter.contains(((CreatureEntity) other).getDefinition()))) {
                counter.increment();
            }
            // TODO break this loop if the test is done
        });
        return operator.test(counter.getValue(), count);
    }
}