package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.FloatComparison;
import com.pixurvival.core.util.IntWrapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureAroundCountAlterationCondition implements AlterationCondition {

    @Valid
    private ElementSet<Structure> structureFilter = new AllElementSet<>();

    @Positive
    private float distance;

    private FloatComparison operator = FloatComparison.GREATER_THAN;

    @Positive
    private int count;

    @Override
    public boolean test(TeamMember entity) {
        IntWrapper counter = new IntWrapper();
        entity.getWorld().getMap().forEachStructure(entity.getPosition(), distance, structure -> {
            if (structureFilter.contains(structure.getDefinition())) {
                counter.increment();
            }
        });
        return operator.test(counter.getValue(), count);
    }
}
