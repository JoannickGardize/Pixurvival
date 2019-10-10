package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;

public interface EffectMovement extends Serializable {

	/**
	 * Only called on the server side.
	 * 
	 * @param entity
	 */
	void initialize(EffectEntity entity);

	void update(EffectEntity entity);

	double getSpeedPotential(EffectEntity entity);

	void writeUpdate(ByteBuffer buffer, EffectEntity entity);

	void applyUpdate(ByteBuffer buffer, EffectEntity entity);
}
