package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.livingEntity.LivingEntity;

public class SilenceAbility extends Ability {

    private static final long serialVersionUID = 1L;

    @Override
    public AbilityData createAbilityData() {
        return new SilenceAbilityData();
    }

    @Override
    public boolean start(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean update(LivingEntity entity) {
        return entity.getWorld().getTime().getTimeMillis() >= ((SilenceAbilityData) getAbilityData(entity)).getEndTime();
    }

    @Override
    public boolean stop(LivingEntity entity) {
        return false;
    }
}