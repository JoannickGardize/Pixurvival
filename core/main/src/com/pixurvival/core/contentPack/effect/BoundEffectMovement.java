package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoundEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private double distance;

	@Override
	public void initialize(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor();
		Vector2 relativeVector = Vector2.fromEuclidean(distance, ancestor.getPosition().angleToward(ancestor.getTargetPosition()));
		entity.setMovementData(relativeVector);
		entity.getPosition().set(entity.getAncestor().getPosition()).add(relativeVector);
	}

	@Override
	public void update(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor();
		if (ancestor != null) {
			entity.getPosition().set(ancestor.getPosition()).add((Vector2) entity.getMovementData());
			if (ancestor instanceof Entity) {
				entity.setMovementSameAs(((Entity) ancestor));
			}
		}
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		return entity.getSpeed();
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		Vector2 relative = (Vector2) entity.getMovementData();
		buffer.putDouble(relative.getX());
		buffer.putDouble(relative.getY());
		entity.getWorld().getEntityPool().writeEntityReference(buffer, (Entity) entity.getAncestor());
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		entity.setMovementData(new Vector2(buffer.getDouble(), buffer.getDouble()));
		entity.setAncestor((TeamMember) entity.getWorld().getEntityPool().readEntityReference(buffer));
	}
}
