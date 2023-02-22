package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.livingEntity.LivingEntity;

import java.nio.ByteBuffer;

public interface AbilityData {

    void write(ByteBuffer buffer, LivingEntity entity);

    void apply(ByteBuffer buffer, LivingEntity entity);

}
