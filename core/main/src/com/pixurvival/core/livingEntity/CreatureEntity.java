package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.PseudoAIUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
public class CreatureEntity extends LivingEntity {

	public static final float OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter @NonNull Creature definition;
	private @Getter @Setter Entity targetEntity;
	private @Getter Vector2 spawnPosition;
	private @Getter TeamMember master = this;

	@Override
	public void initialize() {
		// Add instead of setting base for the case that bonuses are applied at
		// creation.
		getStats().get(StatType.STRENGTH).addToBase(definition.getStrength());
		getStats().get(StatType.AGILITY).addToBase(definition.getAgility());
		getStats().get(StatType.INTELLIGENCE).addToBase(definition.getIntelligence());
		super.initialize();
		behaviorData = new BehaviorData(this);
		if (getWorld().isServer()) {
			currentBehavior = definition.getBehaviorSet().getBehaviors().get(0);
			currentBehavior.begin(this);
			spawnPosition = getPosition().copy();
			if (definition.getLifetime() > 0) {
				getWorld().getActionTimerManager().addActionTimer(new KillCreatureEntityAction(getId()), definition.getLifetime());
			}
		}
	}

	@Override
	public void update() {
		if (getWorld().isServer()) {
			currentBehavior.update(this);
		}
		super.update();
	}

	@Override
	public void takeDamage(float amount) {
		super.takeDamage(amount);
		behaviorData.setTookDamage(true);
	}

	@Override
	protected void onDeath() {
		if (getWorld().isServer() && definition.getItemReward() != null) {
			ItemStack[] items = definition.getItemReward().produce(getWorld().getRandom());
			ItemStackEntity.spawn(getWorld(), items, getPosition());
		}
	}

	public void move(float direction, float forwardFactor) {
		setForwardFactor(forwardFactor);
		if (isSolid()) {
			setMovingAngle(PseudoAIUtils.avoidObstacles(this, direction, (int) OBSTACLE_VISION_DISTANCE, (float) Math.PI / 4));
		} else {
			setMovingAngle(direction);
		}
		setForward(true);
	}

	public void move(float direction) {
		move(direction, 1);
	}

	public void moveIfNotNull(Positionnable entity, float direction) {
		if (entity == null) {
			setForward(false);
		} else {
			move(direction);
		}
	}

	public void getAwayFrom(Positionnable target) {
		moveIfNotNull(target, target.getPosition().angleToward(this.getPosition()));
	}

	public void getAwayFrom(Vector2 position) {
		move(position.angleToward(this.getPosition()));
	}

	public void moveToward(Positionnable target) {
		moveIfNotNull(target, this.angleToward(target));
	}

	public void moveToward(Positionnable target, float randomAngle) {
		moveIfNotNull(target, this.angleToward(target) + (randomAngle == 0 ? 0 : getWorld().getRandom().nextAngle(randomAngle)));
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.CREATURE;
	}

	@Override
	public float getCollisionRadius() {
		return definition.getCollisionRadius();
	}

	@Override
	public AbilitySet getAbilitySet() {
		return definition.getAbilitySet();
	}

	@Override
	public boolean isSolid() {
		return definition.isSolid();
	}

	@Override
	public void writeInitialization(ByteBuffer buffer) {
		buffer.putShort((short) definition.getId());
	}

	@Override
	public void applyInitialization(ByteBuffer buffer) {
		definition = getWorld().getContentPack().getCreatures().get(buffer.getShort());
	}

	public void setMaster(TeamMember master) {
		this.master = master;
		setTeam(master.getTeam());
	}

	@Override
	protected void collisionLockEnded() {
		setForward(false);
	}

	@Override
	public void prepareTargetedAlteration() {
		if (targetEntity != null) {
			getTargetPosition().set(targetEntity.getPosition());
			float predictionBulletSpeed;
			if (getCurrentAbility() instanceof CreatureAlterationAbility && (predictionBulletSpeed = ((CreatureAlterationAbility) getCurrentAbility()).getPredictionBulletSpeed()) > 0) {
				PseudoAIUtils.findTargetPositionPrediction(getPosition(), predictionBulletSpeed, getTargetPosition(), targetEntity.getVelocity());
			}
		}
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}

	@Override
	public void writeRepositoryPart(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(spawnPosition.getX());
		byteBuffer.putFloat(spawnPosition.getY());
	}

	@Override
	public void applyRepositoryPart(ByteBuffer byteBuffer) {
		spawnPosition.set(byteBuffer.getFloat(), byteBuffer.getFloat());
	}
}
