package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.Positionnable;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.creature.behaviorImpl.BehaviorTarget;
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
import com.pixurvival.core.map.HarvestableMapStructure;
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
	private @Getter Vector2 spawnPosition;
	private @Getter TeamMember master = this;
	private @Getter Inventory inventory = EmptyInventoryProxy.getInstance();

	private long creationTime;

	private static final Kryo INVENTORY_KRYO = new Kryo();

	static {
		INVENTORY_KRYO.register(ItemStack.class, new ItemStack.Serializer());
		INVENTORY_KRYO.register(Inventory.class, new Inventory.Serializer());
	}

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
		if (getWorld().isServer()) {
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
		moveIfNotNull(target, this.angleToward(target), obstacleVisionDistance);
	}

	public void moveToward(Positionnable target, float randomAngle) {
		moveIfNotNull(target, this.angleToward(target) + (randomAngle == 0 ? 0 : getWorld().getRandom().nextAngle(randomAngle)));
	}

	public void harvest(HarvestableMapStructure harvestableStructure) {
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
		if (targetEntity != null && targetEntity.isAlive()) {
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
	public void writeRepositoryUpdate(ByteBuffer buffer) {
		buffer.putFloat(getStats().get(StatType.STRENGTH).getBase());
		buffer.putFloat(getStats().get(StatType.AGILITY).getBase());
		buffer.putFloat(getStats().get(StatType.INTELLIGENCE).getBase());
		super.writeRepositoryUpdate(buffer);
		buffer.putFloat(spawnPosition.getX());
		buffer.putFloat(spawnPosition.getY());
		if (getDefinition().getLifetime() > 0) {
			ByteBufferUtils.writePastTime(buffer, getWorld(), creationTime);
		}
		TeamMemberSerialization.writeNullSafe(buffer, master == this ? null : master, true);
		VarLenNumberIO.writePositiveVarInt(buffer, currentBehavior.getId());
		if (definition.getInventorySize() > 0) {
			try (Output output = new Output(buffer.array())) {
				output.setPosition(buffer.position());
				INVENTORY_KRYO.writeObject(output, inventory);
				buffer.position(output.position());
			}
		}
	}

	@Override
	public void applyRepositoryUpdate(ByteBuffer buffer) {
		getStats().get(StatType.STRENGTH).setBase(buffer.getFloat());
		getStats().get(StatType.AGILITY).setBase(buffer.getFloat());
		getStats().get(StatType.INTELLIGENCE).setBase(buffer.getFloat());
		super.applyRepositoryUpdate(buffer);
		spawnPosition = new Vector2(buffer.getFloat(), buffer.getFloat());
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
			try (Input input = new Input(buffer.array())) {
				input.setPosition(buffer.position());
				inventory.set(INVENTORY_KRYO.readObject(input, Inventory.class));
				buffer.position(input.position());
			}
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
