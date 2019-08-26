package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author SharkHendrix
 * @deprecated l'existence de ce type d'EffectMovement est remise en cause, elle
 *             pause des problèmes depuis l'existance des Effect Ancestors, et
 *             n'est peut être pas cohérente dans le style de jeu recherché.
 */
@Deprecated
public class AnchorEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private double distance;

	@Override
	public void initialize(EffectEntity entity) {
		// LivingEntity source = entity.getSource();
		// entity.setMovementData(source.getPosition().angleToward(source.getTargetPosition()));
		// updatePosition(entity);
	}

	@Override
	public void update(EffectEntity entity) {
		// if (entity.getSource() == null) {
		// return;
		// }
		// updatePosition(entity);
		// entity.setForward(entity.getSource().isForward());
		// entity.setMovingAngle(entity.getSource().getMovingAngle());
	}

	private void updatePosition(EffectEntity entity) {
		// entity.getPosition().set(entity.getSource().getPreviousPosition()).addEuclidean(distance,
		// (double) entity.getMovementData());
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		// if (entity.getSource() == null) {
		// return 0;
		// } else {
		// return entity.getSource().getSpeed();
		// }
		return 0;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		// entity.getWorld().getEntityPool().writeEntityReference(buffer,
		// entity.getSource());
		// buffer.putDouble((double) entity.getMovementData());
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		// entity.setSource((LivingEntity)
		// entity.getWorld().getEntityPool().readEntityReference(buffer));
		// entity.setMovementData(buffer.getDouble());
	}
}
