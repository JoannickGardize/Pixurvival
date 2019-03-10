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

	private double health;
	private double aimingAngle;

	private AbilityData[] abilityData;
	private Ability currentAbility;

	@Override
	public void initialize() {
		health = getMaxHealth();
		AbilitySet abilitySet = getAbilitySet();
		abilityData = new AbilityData[abilitySet.size()];
		for (int i = 0; i < abilitySet.size(); i++) {
			abilityData[i] = abilitySet.get(i).createAbilityData();
		}
	}

	@Override
	public void takeDamage(double amount) {
		health -= amount;
		if (health < 0) {
			health = 0;
		}
	}

	@Override
	public void takeHeal(double amount) {
		health += amount;
		if (health > getMaxHealth()) {
			health = getMaxHealth();
		}
	}

	@Override
	public void update() {
		// Only server has the final decision to kill an alive entity
		if (getWorld().isServer() && health <= 0) {
			setAlive(false);
		}

		if (currentAbility != null) {
			if (!currentAbility.canMove() && isForward()) {
				stopCurrentAbility();
			} else if (currentAbility.update(this)) {
				currentAbility = null;
			}
		}

		super.update();
	}

	public AbilityData getAbilityData(int abilityId) {
		return abilityData[abilityId];
	}

	public void startAbility(int abilityId) {
		if (currentAbility != null && (currentAbility.getId() == abilityId || !currentAbility.stop(this))) {
			return;
		}
		if (abilityId == Ability.NONE_ID) {
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
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		buffer.putDouble(getMovingAngle());
		buffer.put(isForward() ? (byte) 1 : (byte) 0);
		buffer.putDouble(getHealth());
		buffer.putDouble(getAimingAngle());

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
		setHealth(buffer.getDouble());
		setAimingAngle(buffer.getDouble());

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
