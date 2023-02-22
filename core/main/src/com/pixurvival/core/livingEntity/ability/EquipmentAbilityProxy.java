package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EquipmentAbilityProxy extends Ability {

    private static final long serialVersionUID = 1L;

    private @NonNull EquipmentAbilityType type;

    @Override
    public boolean start(LivingEntity entity) {
        Ability currentAbility = getCurrentAbility(entity);
        return currentAbility != null && currentAbility.start(entity);
    }

    @Override
    public boolean update(LivingEntity entity) {
        Ability currentAbility = getCurrentAbility(entity);
        return currentAbility == null || currentAbility.update(entity, getAbilityData(entity));
    }

    @Override
    public boolean stop(LivingEntity entity) {
        Ability currentAbility = getCurrentAbility(entity);
        return currentAbility == null || currentAbility.stop(entity);
    }

    @Override
    public AbilityData createAbilityData() {
        return new CooldownAbilityData();
    }

    private Ability getCurrentAbility(LivingEntity entity) {
        return type.getAbilityGetter().apply(((PlayerEntity) entity).getEquipment());
    }
}
