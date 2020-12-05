package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackToOriginEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private float speed;

	@Override
	public void initialize(EffectEntity entity) {
		entity.getPosition().set(entity.getAncestor().getPosition());
		entity.setForward(true);
	}

	@Override
	public void update(EffectEntity entity) {
		TeamMember origin = entity.getOrigin();
		if (origin != null) {
			entity.setMovingAngle(entity.angleToward(origin));
		}
	}

	@Override
	public float getSpeedPotential(EffectEntity entity) {
		return entity.isAlive() ? speed : 0;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		TeamMemberSerialization.write(buffer, entity.getOrigin(), false);
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		entity.setAncestor(TeamMemberSerialization.read(buffer, entity.getWorld(), false));
	}
}
