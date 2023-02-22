package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.livingEntity.LivingEntity;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class SilenceAbilityData implements AbilityData {

    private long endTime;

    @Override
    public void write(ByteBuffer buffer, LivingEntity entity) {

    }

    @Override
    public void apply(ByteBuffer buffer, LivingEntity entity) {

    }

}
