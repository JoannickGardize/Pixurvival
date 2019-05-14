package com.pixurvival.core.livingEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

public class CooldownAbilityData implements AbilityData {

	@Getter
	@Setter
	private long readyTimeMillis;

	@Override
	public void write(ByteBuffer buffer, LivingEntity entity) {
		buffer.putLong(readyTimeMillis);
	}

	@Override
	public void apply(ByteBuffer buffer, LivingEntity entity) {
		readyTimeMillis = buffer.getLong();
	}

}