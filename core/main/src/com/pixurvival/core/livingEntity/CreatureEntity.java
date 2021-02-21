package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
import com.pixurvival.core.contentPack.creature.behaviorImpl.VanishBehavior;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.EmptyInventoryProxy;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;
import com.pixurvival.core.livingEntity.ability.HarvestAbilityData;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.PseudoAIUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
public class CreatureEntity extends LivingEntity {

	public static final float DEFAULT_OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter @NonNull Creature definition;
	private @Getter @Setter Entity targetEntity;
	private @Getter TeamMember master = this;
	private @Getter Inventory inventory = EmptyInventoryProxy.getInstance();
	private boolean targetedAlterationPrepared = false;

	private long creationTime;

	@Override
	public void initialize() {
		// Add instead of setting base for the case that bonuses are applied at
		// creation.
		getStats().get(StatType.STRENGTH).addToBase(definition.getStrength());
		getStats().get(StatType.AGILITY).addToBase(definition.getAgility());
		getStats().get(StatType.INTELLIGENCE).addToBase(definition.getIntelligence());
		super.initialize();
		behaviorData = new BehaviorData(this);
		if (getWorld().isServer() && definition.getInventorySize() > 0) {
			inventory = new Inventory(definition.getInventorySize());
		}
	}

	@Override
	public void initializeAtCreation() {
		super.initializeAtCreation();
		// TODO remove this useless if (and test)
		if (getWorld().isServer()) {
			creationTime = getWorld().getTime().getTimeMillis();
			currentBehavior = definition.getBehaviorSet().getBehaviors().get(0);
			currentBehavior.begin(this);
			if (definition.getLifetime() > 0) {
				getWorld().getActionTimerManager().addActionTimer(new KillCreatureEntityAction(getId()), definition.getLifetime());
			}
		}
	}

	@Override
	public void update() {
		if (getWorld().isServer()) {
			targetedAlterationPrepared = false;
			currentBehavior.update(this);
		}
		super.update();
	}

	@Override
	public void takeDamage(float amount, DamageAttributes attributes) {
		super.takeDamage(amount, attributes);
		behaviorData.setTookDamage(true);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		if (getWorld().isServer()) {
			if (behaviorData.getCustomData() != VanishBehavior.VANISH_MARKER) {
				if (definition.getItemReward() != null) {
					ItemStack[] items = definition.getItemReward().produce(getWorld().getRandom());
					ItemStackEntity.spawn(getWorld(), items, getPosition());
				}
				for (int i = 0; i < inventory.size(); i++) {
					ItemStack itemStack = inventory.getSlot(i);
					if (itemStack != null) {
						ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
						itemStackEntity.getPosition().set(getPosition());
						getWorld().getEntityPool().addNew(itemStackEntity);
						itemStackEntity.spawnRandom();
					}
				}
			}
			currentBehavior.end(this);
		}
	}

	public void move(float direction) {
		move(direction, 1);
	}

	public void move(float direction, float forwardFactor) {
		move(direction, forwardFactor, (int) DEFAULT_OBSTACLE_VISION_DISTANCE);
	}

	public void move(float direction, float forwardFactor, int obstacleVisionDistance) {
		setForwardFactor(forwardFactor);
		if (isSolid() && obstacleVisionDistance > 0) {
			setMovingAngle(PseudoAIUtils.avoidObstacles(this, direction, (int) Math.min(DEFAULT_OBSTACLE_VISION_DISTANCE, obstacleVisionDistance), (float) Math.PI / 4));
		} else {
			setMovingAngle(direction);
		}
		setForward(true);
	}

	public void moveIfNotNull(Positionnable entity, float direction) {
		if (entity == null) {
			setForward(false);
		} else {
			move(direction, 1);
		}
	}

	public void moveIfNotNull(Positionnable entity, float direction, float obstacleVisionDistance) {
		if (entity == null) {
			setForward(false);
		} else {
			move(direction, 1, (int) obstacleVisionDistance);
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

	public void moveTowardPrecisely(Positionnable target, float obstacleVisionDistance) {
		moveTowardPrecisely(target, obstacleVisionDistance, 0);
	}

	public void moveTowardPrecisely(Positionnable target, float obstacleVisionDistance, float randomAngle) {
		moveIfNotNull(target, randomizedTargetAngle(target, randomAngle), obstacleVisionDistance);
	}

	public void moveToward(Positionnable target, float randomAngle) {
		moveIfNotNull(target, randomizedTargetAngle(target, randomAngle));
	}

	private float randomizedTargetAngle(Positionnable target, float randomAngle) {
		return this.angleToward(target) + (randomAngle == 0 ? 0 : getWorld().getRandom().nextAngle(randomAngle));
	}

	public void harvest(HarvestableStructureEntity harvestableStructure) {
		int abilityId = getDefinition().getHarvestAbilityId();
		((HarvestAbilityData) getAbilityData(abilityId)).setStructure(harvestableStructure);
		startAbility(abilityId);
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
		return definition.getEffectiveAbilitySet();
	}

	@Override
	public boolean isSolid() {
		return definition.isSolid();
	}

	@Override
	public void writeInitialization(ByteBuffer buffer) {
		VarLenNumberIO.writePositiveVarInt(buffer, definition.getId());
	}

	@Override
	public void applyInitialization(ByteBuffer buffer) {
		definition = getWorld().getContentPack().getCreatures().get(VarLenNumberIO.readPositiveVarInt(buffer));
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
		if (!targetedAlterationPrepared && targetEntity != null && targetEntity.isAlive()) {
			getTargetPosition().set(targetEntity.getPosition());
			float predictionBulletSpeed;
			if (getCurrentAbility() instanceof CreatureAlterationAbility && (predictionBulletSpeed = ((CreatureAlterationAbility) getCurrentAbility()).getPredictionBulletSpeed()) > 0) {
				PseudoAIUtils.findTargetPositionPrediction(getPosition(), predictionBulletSpeed, getTargetPosition(), targetEntity.getVelocity());
			}
			targetedAlterationPrepared = true;
		}
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}

	@Override
	public void writeRepositoryUpdate(ByteBuffer buffer) {
		super.writeRepositoryUpdate(buffer);
		if (getDefinition().getLifetime() > 0) {
			ByteBufferUtils.writePastTime(buffer, getWorld(), creationTime);
		}
		TeamMemberSerialization.writeNullSafe(buffer, master == this ? null : master, true);
		VarLenNumberIO.writePositiveVarInt(buffer, currentBehavior.getId());
		if (definition.getInventorySize() > 0) {
			inventory.write(buffer);
		}
	}

	@Override
	public void applyRepositoryUpdate(ByteBuffer buffer) {
		super.applyRepositoryUpdate(buffer);
		if (getDefinition().getLifetime() > 0) {
			creationTime = ByteBufferUtils.readPastTime(buffer, getWorld());
			if (getWorld().getTime().getTimeMillis() - creationTime >= getDefinition().getLifetime()) {
				setAlive(false);
			}
			// If this is not time to die, that means that an ActionTimer to
			// kill it on time is still present
		}
		TeamMember newMaster = TeamMemberSerialization.readNullSafe(buffer, getWorld(), true);
		if (newMaster != null) {
			master = newMaster;
		}
		currentBehavior = definition.getBehaviorSet().getBehaviors().get(VarLenNumberIO.readPositiveVarInt(buffer));
		// TODO smart reset behavior
		currentBehavior.begin(this);
		if (definition.getInventorySize() > 0) {
			inventory.apply(getWorld(), buffer);
		}
		addHealthAdapterListener();
	}

	@Override
	protected float getCollisionLockAngle() {
		if (targetEntity != null) {
			return angleToward(targetEntity);
		} else if (master != this) {
			return angleToward(master);
		} else {
			Entity e = BehaviorTarget.CLOSEST_ENNEMY.getEntityGetter().apply(this);
			if (e != null) {
				return angleToward(e);
			} else {
				return super.getCollisionLockAngle();
			}
		}
	}
}
