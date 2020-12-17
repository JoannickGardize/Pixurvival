package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoundEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	@Positive
	private float distance;

	@Override
	public void initialize(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor();
		Vector2 relativeVector = Vector2.fromEuclidean(distance, ancestor.getPosition().angleToward(ancestor.getTargetPosition()));
		entity.setMovementData(relativeVector);
		entity.getPosition().set(entity.getAncestor().getPosition()).add(relativeVector);
	}

	@Override
	public void update(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor().findIfNotFound();
		if (ancestor != null) {
			entity.getPosition().set(ancestor.getPosition()).add((Vector2) entity.getMovementData());
			if (ancestor instanceof Entity) {
				entity.setMovementSameAs(((Entity) ancestor));
			}
		}
	}

	@Override
	public float getSpeedPotential(EffectEntity entity) {
		return entity.getSpeed();
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		Vector2 relative = (Vector2) entity.getMovementData();
		buffer.putFloat(relative.getX());
		buffer.putFloat(relative.getY());
		TeamMemberSerialization.write(buffer, entity.getAncestor(), false);
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		entity.setMovementData(new Vector2(buffer.getFloat(), buffer.getFloat()));
		entity.setAncestor(TeamMemberSerialization.read(buffer, entity.getWorld(), false));
	}

	@Override
	public boolean isDestroyWithAncestor() {
		return true;
	}
}
