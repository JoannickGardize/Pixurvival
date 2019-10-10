package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilityData;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.alteration.PersistentAlteration;
import com.pixurvival.core.livingEntity.alteration.PersistentAlterationEntry;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class LivingEntity extends Entity implements Damageable, TeamMember {

	public static final int SILENCE_ABILITY_ID = 0;

	private float health;

	private StatSet stats = new StatSet();

	private AbilityData[] abilityData;
	private Ability currentAbility;

	private Vector2 targetPosition = new Vector2();

	private List<PersistentAlterationEntry> persistentAlterationEntries = new ArrayList<>();

	private @Setter Team team = TeamSet.WILD_TEAM;

	private @Setter boolean movementChangeEnabled = true;
	private @Setter SpriteSheet overridingSpriteSheet = null;

	public LivingEntity() {
	}

	public void setFixedMovement(double movingAngle, double speed) {
		movementChangeEnabled = true;
		setMovingAngle(movingAngle);
		setSpeed(speed * getForwardFactor() * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor());
		setForward(true);
		updateVelocity();
		movementChangeEnabled = false;
	}

	public void stopFixedMovement() {
		movementChangeEnabled = true;
		fixedMovementEnded();
	}

	protected void fixedMovementEnded() {
	}

	@Override
	public void initialize() {
		stats.get(StatType.MAX_HEALTH).addListener(s -> {
			if (getHealth() > s.getValue()) {
				setHealth(s.getValue());
			}
		});
		health = getMaxHealth();
		initializeAbilityData();
	}

	private void initializeAbilityData() {
		AbilitySet<? extends Ability> abilitySet = getAbilitySet();
		abilityData = new AbilityData[abilitySet.size()];
		for (int i = 0; i < abilitySet.size(); i++) {
			abilityData[i] = abilitySet.get(i).createAbilityData();
		}
	}

	public void setHealth(float health) {
		if (health != this.health) {
			this.health = health;
			setStateChanged(true);
		}
	}

	@Override
	public void setForward(boolean forward) {
		if (movementChangeEnabled && forward != isForward()) {
			super.setForward(forward);
			setStateChanged(true);
		}
	}

	@Override
	public void setMovingAngle(double movingAngle) {
		if (movementChangeEnabled && movingAngle != getMovingAngle()) {
			super.setMovingAngle(movingAngle);
			setStateChanged(true);
		}
	}

	@Override
	public float getMaxHealth() {
		return stats.getValue(StatType.MAX_HEALTH);
	}

	@Override
	public double getSpeedPotential() {
		if (movementChangeEnabled) {
			if (isSolid()) {
				return stats.getValue(StatType.SPEED) * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor();
			} else {
				return stats.getValue(StatType.SPEED);
			}
		} else {
			return getSpeed();
		}
	}

	@Override
	public void takeDamage(float amount) {
		takeTrueDamage(amount * (1 - stats.getValue(StatType.ARMOR)));
	}

	public void takeTrueDamage(float amount) {
		health -= amount;
		if (health < 0) {
			health = 0;
		}
		setStateChanged(true);
	}

	@Override
	public void takeHeal(float amount) {
		health += amount;
		if (health > getMaxHealth()) {
			health = getMaxHealth();
		}
		setStateChanged(true);
	}

	public void applyPersistentAlteration(TeamMember source, PersistentAlteration alteration) {
		PersistentAlterationEntry entry = new PersistentAlterationEntry(source, alteration);
		switch (alteration.getStackPolicy()) {
		case IGNORE:
			if (!persistentAlterationEntries.contains(entry)) {
				persistentAlterationEntries.add(entry);
				beginPersistentAlteration(entry);
			}
			break;
		case REPLACE:
			int index = persistentAlterationEntries.indexOf(entry);
			if (index == -1) {
				persistentAlterationEntries.add(entry);
			} else {
				PersistentAlterationEntry oldEntry = persistentAlterationEntries.get(index);
				oldEntry.getAlteration().end(oldEntry.getSource(), this);
				persistentAlterationEntries.set(index, entry);
			}
			beginPersistentAlteration(entry);
			break;
		case STACK:
			persistentAlterationEntries.add(entry);
			beginPersistentAlteration(entry);
			break;
		default:
			break;
		}
	}

	private void beginPersistentAlteration(PersistentAlterationEntry entry) {
		entry.setTermTimeMillis(entry.getAlteration().getDuration() + getWorld().getTime().getTimeMillis());
		entry.getAlteration().begin(entry.getSource(), this);
	}

	@Override
	public void update() {
		long timeMillis = getWorld().getTime().getTimeMillis();
		persistentAlterationEntries.removeIf(entry -> {
			entry.getAlteration().update(entry.getSource(), this);
			if (timeMillis >= entry.getTermTimeMillis()) {
				entry.getAlteration().end(entry.getSource(), this);
				return true;
			}
			return false;
		});

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
		super.update();
	}

	public AbilityData getAbilityData(int abilityId) {
		return abilityData[abilityId];
	}

	public AbilityData setAbilityData(int abilityId) {
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
		setStateChanged(true);
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
		buffer.putShort((short) getTeam().getId());
		ByteBufferUtils.writeElementOrNull(buffer, getOverridingSpriteSheet());
		if (getCurrentAbility() == null) {
			buffer.put(Ability.NONE_ID);
		} else {
			byte id = getCurrentAbility().getId();
			buffer.put(id);
			getAbilityData(id).write(buffer, this);
		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setMovingAngle(buffer.getDouble());
		setForward(buffer.get() == 1);
		setHealth(buffer.getFloat());
		setTeam(getWorld().getTeamSet().get(buffer.getShort()));
		ByteBufferUtils.readElementOrNull(buffer, getWorld().getContentPack().getSpriteSheets());
		byte abilityId = buffer.get();
		if (abilityId == Ability.NONE_ID) {
			stopCurrentAbility();
		} else {
			getAbilityData(abilityId).apply(buffer, this);
			startAbility(abilityId);
		}
	}

	public abstract AbilitySet<? extends Ability> getAbilitySet();
}
