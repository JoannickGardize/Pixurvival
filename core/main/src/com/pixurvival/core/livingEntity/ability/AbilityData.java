package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.livingEntity.LivingEntity;

public interface AbilityData {

	void write(ByteBuffer buffer, LivingEntity entity);

	void apply(ByteBuffer buffer, LivingEntity entity);

}
