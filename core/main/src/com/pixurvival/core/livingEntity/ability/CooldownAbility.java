package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.LivingEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CooldownAbility extends Ability {

    private static final long serialVersionUID = 1L;

    @Positive
    private long cooldown;

    @Override
    public boolean start(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean update(LivingEntity entity) {
        CooldownAbilityData data = ((CooldownAbilityData) getAbilityData(entity));
        return update(entity, data);
    }

    @Override
    public boolean update(LivingEntity entity, AbilityData data) {

        CooldownAbilityData cooldownData = (CooldownAbilityData) data;
        long readyTimeMillis = cooldownData.getReadyTimeMillis();
        long currentTimeMillis = entity.getWorld().getTime().getTimeMillis();
        if (currentTimeMillis >= readyTimeMillis && fire(entity)) {
            cooldownData.setReadyTimeMillis(currentTimeMillis + cooldown);
        }
        return false;
    }

    @Override
    public boolean stop(LivingEntity entity) {
        return true;
    }

    @Override
    public AbilityData createAbilityData() {
        return new CooldownAbilityData();
    }

    public abstract boolean fire(LivingEntity entity);
}
