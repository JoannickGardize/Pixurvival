package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.Time;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.OrientationType;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.alteration.CheckListHolder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class EffectEntity extends Entity implements CheckListHolder, SourceProvider {

	private @Getter Effect definition;

	private @Getter @Setter LivingEntity source;

	private @Getter @Setter Object movementData;

	private @Getter float orientation;

	private long termTimeMillis;

	private Collection<Object> checkList;

	public EffectEntity(Effect definition, LivingEntity source) {
		this.definition = definition;
		this.source = source;
	}

	@Override
	public void initialize() {
		definition.getMovement().initialize(this);
		if (getWorld().isServer()) {
			if (definition.getOrientation() == OrientationType.FROM_SOURCE) {
				orientation = (float) source.getPosition().angleToward(source.getTargetPosition());
			}
			termTimeMillis = getWorld().getTime().getTimeMillis() + Time.secToMillis(definition.getDuration());
		}
	}

	@Override
	public void update() {
		definition.getMovement().update(this);
		if (getWorld().isServer()) {
			if (getWorld().getTime().getTimeMillis() >= termTimeMillis || definition.isSolid() && getWorld().getMap().collide(this)) {
				setAlive(false);
				return;
			}
			processEffectTarget();
		}
		super.update();
	}

	private void processEffectTarget() {
		for (EffectTarget effectTarget : definition.getTargets()) {
			source.forEach(effectTarget.getTargetType(), GameConstants.EFFECT_TARGET_DISTANCE_CHECK, e -> {
				if (collideDynamic(e)) {
					effectTarget.getAlterations().forEach(a -> a.apply(this, e));
					if (effectTarget.isDestroyWhenCollide()) {
						setAlive(false);
					}
				}
			});
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.EFFECT;
	}

	@Override
	public double getCollisionRadius() {
		return definition.getCollisionRadius();
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		buffer.putShort((short) definition.getId());
		buffer.putDouble(getPosition().getX());
		buffer.putDouble(getPosition().getY());
		buffer.putFloat(orientation);
		buffer.put(isForward() ? (byte) 1 : (byte) 0);
		buffer.putDouble(getMovingAngle());

	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		definition = getWorld().getContentPack().getEffects().get(buffer.getShort());
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		orientation = buffer.getFloat();
		setForward(buffer.get() == 1);
		setMovingAngle(buffer.getDouble());
	}

	@Override
	public double getSpeedPotential() {
		return definition.getMovement().getSpeedPotential(this);
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public Collection<Object> getCheckList() {
		if (checkList == null) {
			checkList = new ArrayList<>(3);
		}
		return checkList;
	}
}
