package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.Entity;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilityData;
import com.pixurvival.core.livingEntity.ability.AbilitySet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LivingEntity extends Entity implements Damageable {

	private float health;
	private float aimingAngle;

	private StatSet stats = new StatSet();

	private AbilityData[] abilityData;
	private Ability currentAbility;

	@Override
	public void initialize() {
		stats.get(StatType.MAX_HEALTH).addListener(s -> {
			if (getHealth() > s.getValue()) {
				setHealth(s.getValue());
			}
		});
		health = getMaxHealth();
		AbilitySet abilitySet = getAbilitySet();
		abilityData = new AbilityData[abilitySet.size()];
		for (int i = 0; i < abilitySet.size(); i++) {
			abilityData[i] = abilitySet.get(i).createAbilityData();
		}
	}

	@Override
	public float getMaxHealth() {
		return stats.getValue(StatType.MAX_HEALTH);
	}

	@Override
	public double getSpeedPotential() {
		return stats.getValue(StatType.SPEED) * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor();
	}

	@Override
	public void takeDamage(float amount) {
		health -= amount;
		if (health < 0) {
			health = 0;
		}
	}

	@Override
	public void takeHeal(float amount) {
		health += amount;
		if (health > getMaxHealth()) {
			health = getMaxHealth();
		}
	}

	@Override
	public void update() {
		super.update();

		// Only server has the final decision to kill an alive entity
		if (health <= 0 && getWorld().isServer()) {
			setAlive(false);
		}

		if (currentAbility != null) {
			if (!currentAbility.canMove() && isForward()) {
				stopCurrentAbility();
			} else if (currentAbility.update(this)) {
				currentAbility = null;
			}
		}

	}

	public AbilityData getAbilityData(int abilityId) {
		return abilityData[abilityId];
	}

	public void startAbility(int abilityId) {
		if (currentAbility != null && (currentAbility.getId() == abilityId || !currentAbility.stop(this))) {
			return;
		}
		if (abilityId < 0 || abilityId >= getAbilitySet().size()) {
			currentAbility = null;
		} else {
			Ability ability = getAbilitySet().get(abilityId);
			if (!ability.start(this)) {
				return;
			}
			currentAbility = ability;
		}

	}

	public void stopCurrentAbility() {
		if (currentAbility != null && currentAbility.stop(this)) {
			currentAbility = null;
		}
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		// normal part
		buffer.putDouble(getPosition().getX());
		buffer.putDouble(getPosition().getY());
		buffer.putDouble(getMovingAngle());
		buffer.put(isForward() ? (byte) 1 : (byte) 0);
		buffer.putFloat(getHealth());
		buffer.putFloat(getAimingAngle());

		if (getCurrentAbility() == null) {
			buffer.put(Ability.NONE_ID);
		} else {
			byte id = getCurrentAbility().getId();
			buffer.put(id);
			getAbilityData(id).write(buffer);
		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setMovingAngle(buffer.getDouble());
		setForward(buffer.get() == 1);
		setHealth(buffer.getFloat());
		setAimingAngle(buffer.getFloat());

		byte abilityId = buffer.get();
		if (abilityId == Ability.NONE_ID) {
			stopCurrentAbility();
		} else {
			getAbilityData(abilityId).apply(buffer, this);
			startAbility(abilityId);
		}
	}

	public abstract AbilitySet getAbilitySet();
}
