package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackToOriginEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private float speed;

	@Override
	public void initialize(EffectEntity entity) {
		entity.setMovementData(entity.getOrigin());
		entity.getPosition().set(entity.getAncestor().getPosition());
		entity.setForward(true);
	}

	@Override
	public void update(EffectEntity entity) {
		entity.setMovingAngle(entity.angleToward((TeamMember) entity.getMovementData()));
	}

	@Override
	public float getSpeedPotential(EffectEntity entity) {
		return entity.isAlive() ? speed : 0;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {

		entity.getWorld().getEntityPool().writeEntityReference(buffer, (Entity) entity.getMovementData());
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		entity.setMovementData(entity.getWorld().getEntityPool().readEntityReference(buffer));
	}

}
