package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntitySearchUtils;
import com.pixurvival.core.livingEntity.CreatureEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class Behavior extends NamedIdentifiedElement {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<ChangeCondition> changeConditions = new ArrayList<>();

    @Positive
    private int abilityToUseId = 0;

    private transient int effectiveAbilityToUse;

    @Override
    public void initialize() {
        if (abilityToUseId == 0) {
            effectiveAbilityToUse = -1;
        } else {
            effectiveAbilityToUse = abilityToUseId;
        }
    }

    public void update(CreatureEntity creature) {
        BehaviorData behaviorData = creature.getBehaviorData();
        if (creature.getWorld().getTime().getTimeMillis() >= behaviorData.getNextUpdateTimeMillis()) {
            nextBehavior(creature);
            behaviorData.beforeStep();
            // Get the current behavior in case it has been changed
            creature.getCurrentBehavior().step(creature);
        }
    }

    public void begin(CreatureEntity creature) {
        creature.getBehaviorData().reset();
        creature.startAbility(effectiveAbilityToUse);
        creature.setTargetEntity(null);
    }

    protected abstract void step(CreatureEntity creature);

    /**
     * Called when the behavior ends, in any way, even for death of the creature.
     *
     * @param creature
     */
    public void end(CreatureEntity creature) {
        // Nothing by default
    }

    private void nextBehavior(CreatureEntity creature) {
        for (ChangeCondition condition : changeConditions) {
            if (condition.test(creature)) {
                pass(creature, condition);
                break;
            }
        }
        creature.getBehaviorData().updateForNextChangeConditionCheck();
    }

    private void pass(CreatureEntity creature, ChangeCondition condition) {
        end(creature);
        Behavior behavior = condition.getNextBehavior();
        creature.setCurrentBehavior(behavior);
        behavior.begin(creature);
        affectNeighbors(condition, creature);
    }

    private void affectNeighbors(ChangeCondition condition, CreatureEntity creature) {
        if (condition.getAffectedNeighborsDistance() > 0) {
            float squaredDistance = condition.getAffectedNeighborsDistance() * condition.getAffectedNeighborsDistance();
            EntitySearchUtils.forEach(creature, EntityGroup.CREATURE, condition.getAffectedNeighborsDistance(), e -> {
                CreatureEntity c = (CreatureEntity) e;
                if (c.getDefinition() == creature.getDefinition() && c.getCurrentBehavior() == this && e.distanceSquared(creature) <= squaredDistance) {
                    pass(c, condition);
                }
            });
        }
    }
}
