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

	public static final byte UPDATE_CONTENT_MASK_STATS = 1;
	public static final byte UPDATE_CONTENT_MASK_OTHERS = 2;
	public static final byte UPDATE_CONTENT_MASK_FORWARD = 4;
	public static final byte UPDATE_CONTENT_MASK_MOVEMENT_CHANGE_ENABLED = 8;
	public static final byte UPDATE_CONTENT_MASK_ALL = UPDATE_CONTENT_MASK_STATS | UPDATE_CONTENT_MASK_OTHERS;

	private byte updateContentFlags = UPDATE_CONTENT_MASK_ALL;

	private float health;

	private StatSet stats = new StatSet();

	private AbilityData[] abilityData;
	private Ability currentAbility;

	private Vector2 targetPosition = new Vector2();

	private List<PersistentAlterationEntry> persistentAlterationEntries = new ArrayList<>();

	private @Setter Team team = TeamSet.WILD_TEAM;

	private @Setter boolean movementChangeEnabled = true;
	private SpriteSheet overridingSpriteSheet = null;

	private long stunTermTime = 0;
	private long invincibleTermTime = 0;

	@Override
	protected boolean canForward() {
		return getWorld().getTime().getTimeMillis() >= stunTermTime;
	}

	@Override
	public void setStateChanged(boolean stateChanged) {
		if (!stateChanged) {
			updateContentFlags = 0;
		}
		super.setStateChanged(stateChanged);
	}

	public void addUpdateContentMask(byte mask) {
		updateContentFlags |= mask;
	}

	public void stun(long duration) {
		long term = duration + getWorld().getTime().getTimeMillis();
		if (term > stunTermTime) {
			stunTermTime = term;
			setStateChanged(true);
			addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
		}
	}

	public void setInvincible(long duration) {
		long term = duration + getWorld().getTime().getTimeMillis();
		if (term > invincibleTermTime) {
			invincibleTermTime = term;
		}
	}

	public void setFixedMovement(double movingAngle, double speed) {
		movementChangeEnabled = true;
		setMovingAngle(movingAngle);
		setSpeed(speed * getForwardFactor() * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor());
		setForward(true);
		updateVelocity();
		movementChangeEnabled = false;
	}

	@Override
	public boolean setSpeed(double speed) {
		if (movementChangeEnabled && super.setSpeed(speed)) {
			setStateChanged(true);
			return true;
		}
		return false;
	}

	public void setOverridingSpriteSheet(SpriteSheet spriteSheet) {
		if (overridingSpriteSheet != spriteSheet) {
			overridingSpriteSheet = spriteSheet;
			setStateChanged(true);
			addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
		}
	}

	public void stopFixedMovement() {
		movementChangeEnabled = true;
		fixedMovementEnded();
	}

	protected void fixedMovementEnded() {
	}

	@Override
	public void initialize() {
		super.initialize();
		health = getMaxHealth();
		if (getWorld().isServer()) {
			stats.get(StatType.MAX_HEALTH).addListener(s -> {
				if (getHealth() > s.getValue()) {
					setHealth(s.getValue());
				}
			});
			stats.addListener(s -> {
				setStateChanged(true);
				addUpdateContentMask(UPDATE_CONTENT_MASK_STATS);
			});
		}
		initializeAbilityData();
	}

	private void initializeAbilityData() {
		AbilitySet abilitySet = getAbilitySet();
		abilityData = new AbilityData[abilitySet.size()];
		for (int i = 0; i < abilitySet.size(); i++) {
			abilityData[i] = abilitySet.get(i).createAbilityData();
		}
	}

	public void teleport(Vector2 position) {
		if (!getWorld().getMap().collide(getPosition().getX(), getPosition().getY(), getCollisionRadius())) {
			getPosition().set(position);
		}
		setStateChanged(true);
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
	public void setForwardFactor(double forwardFactor) {
		if (forwardFactor != getForwardFactor()) {
			super.setForwardFactor(forwardFactor);
			setStateChanged(true);
			addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
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
		if (getWorld().getTime().getTimeMillis() < invincibleTermTime) {
			return;
		}
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
				oldEntry.getAlteration().end(oldEntry.getSource(), this, oldEntry.getData());
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
		// Begin before fixing the term time, because some type of
		// PersistentAlteration
		// fixes the term during this method
		entry.setData(entry.getAlteration().begin(entry.getSource(), this));
		entry.setTermTimeMillis(entry.getAlteration().getDuration() + getWorld().getTime().getTimeMillis());
	}

	@Override
	public void update() {
		long timeMillis = getWorld().getTime().getTimeMillis();
		persistentAlterationEntries.removeIf(entry -> {
			entry.setData(entry.getAlteration().update(entry.getSource(), this, entry.getData()));
			if (timeMillis >= entry.getTermTimeMillis()) {
				entry.getAlteration().end(entry.getSource(), this, entry.getData());
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
		addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
	}

	public void stopCurrentAbility() {
		if (currentAbility != null && currentAbility.stop(this)) {
			currentAbility = null;
			setStateChanged(true);
			addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
		}
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, boolean full) {

		// Building update flags

		byte updateFlagsToSend = full ? UPDATE_CONTENT_MASK_ALL : updateContentFlags;
		if (isForward()) {
			updateFlagsToSend |= UPDATE_CONTENT_MASK_FORWARD;
		}
		if (movementChangeEnabled) {
			updateFlagsToSend |= UPDATE_CONTENT_MASK_MOVEMENT_CHANGE_ENABLED;
		}
		buffer.put(updateFlagsToSend);

		// normal part

		buffer.putDouble(getPosition().getX());
		buffer.putDouble(getPosition().getY());
		buffer.putDouble(getMovingAngle());
		if (!movementChangeEnabled) {
			buffer.putDouble(getSpeed());
		}
		buffer.putFloat(getHealth());

		// stats part

		if ((updateFlagsToSend & UPDATE_CONTENT_MASK_STATS) != 0) {
			buffer.putFloat(getStats().getValue(StatType.STRENGTH));
			buffer.putFloat(getStats().getValue(StatType.AGILITY));
			buffer.putFloat(getStats().getValue(StatType.INTELLIGENCE));
			buffer.putFloat(getStats().getValue(StatType.MAX_HEALTH));
			buffer.putFloat(getStats().getValue(StatType.ARMOR));
			buffer.putFloat(getStats().getValue(StatType.SPEED));
		}

		// others part

		if ((updateFlagsToSend & UPDATE_CONTENT_MASK_OTHERS) != 0) {
			buffer.putLong(stunTermTime);
			buffer.putShort((short) getTeam().getId());
			ByteBufferUtils.writeElementOrNull(buffer, overridingSpriteSheet);
			buffer.putDouble(getForwardFactor());
			if (getCurrentAbility() == null) {
				buffer.put(Ability.NONE_ID);
			} else {
				byte id = getCurrentAbility().getId();
				buffer.put(id);
				getAbilityData(id).write(buffer, this);
			}
		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {

		byte updateContentFlag = buffer.get();

		// normal part

		getPosition().set(buffer.getDouble(), buffer.getDouble());
		if ((updateContentFlag & UPDATE_CONTENT_MASK_MOVEMENT_CHANGE_ENABLED) != 0) {
			stopFixedMovement();
			setForward((updateContentFlag & UPDATE_CONTENT_MASK_FORWARD) != 0);
			setMovingAngle(buffer.getDouble());
		} else {
			setFixedMovement(buffer.getDouble(), buffer.getDouble());
		}
		setHealth(buffer.getFloat());

		// stats part
		if ((updateContentFlag & UPDATE_CONTENT_MASK_STATS) != 0) {
			getStats().get(StatType.STRENGTH).setValue(buffer.getFloat());
			getStats().get(StatType.AGILITY).setValue(buffer.getFloat());
			getStats().get(StatType.INTELLIGENCE).setValue(buffer.getFloat());
			getStats().get(StatType.MAX_HEALTH).setValue(buffer.getFloat());
			getStats().get(StatType.ARMOR).setValue(buffer.getFloat());
			getStats().get(StatType.SPEED).setValue(buffer.getFloat());
		}

		// others part

		if ((updateContentFlag & UPDATE_CONTENT_MASK_OTHERS) != 0) {
			stunTermTime = buffer.getLong();
			setTeam(getWorld().getTeamSet().get(buffer.getShort()));
			overridingSpriteSheet = ByteBufferUtils.readElementOrNull(buffer, getWorld().getContentPack().getSpriteSheets());
			setForwardFactor(buffer.getDouble());
			byte abilityId = buffer.get();
			if (abilityId == Ability.NONE_ID) {
				stopCurrentAbility();
			} else {
				getAbilityData(abilityId).apply(buffer, this);
				startAbility(abilityId);
			}
		}
	}

	public void beforeTargetedAlteration() {
		// for overriding
	}

	public abstract AbilitySet getAbilitySet();
}
