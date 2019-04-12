package com.pixurvival.core.contentPack.effect;

import java.io.Serializable;

import com.pixurvival.core.entity.EffectEntity;

public interface EffectMovement extends Serializable {

	void initialize(EffectEntity entity);

	void update(EffectEntity entity);

	double getSpeedPotential();
}
