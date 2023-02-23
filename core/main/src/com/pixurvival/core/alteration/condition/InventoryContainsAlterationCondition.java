package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.InventoryUtils;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.FloatComparison;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryContainsAlterationCondition implements AlterationCondition {

    @Valid
    private ElementSet<Item> items = new AllElementSet<>();
    private FloatComparison operator = FloatComparison.GREATER_THAN;
    @Positive
    private int value;

    @Override
    public boolean test(TeamMember entity) {
        if (!(entity instanceof InventoryHolder)) {
            return false;
        }
        return InventoryUtils.testContent((InventoryHolder) entity, items, operator, value);
    }


}
