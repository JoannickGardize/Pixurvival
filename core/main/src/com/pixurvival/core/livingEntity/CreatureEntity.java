package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.util.MoveUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
public class CreatureEntity extends LivingEntity {

	private static final AbilitySet EMPTY_ABILITY_SET = new AbilitySet();

	public static final double OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter @NonNull Creature definition;

	@Override
	public void initialize() {
		if (getWorld().isServer()) {
			currentBehavior = definition.getBehaviorSet().getBehaviors().get(0);
			currentBehavior.begin(this);
		}
		getStats().get(StatType.STRENGTH).setBase(definition.getStrength());
		getStats().get(StatType.AGILITY).setBase(definition.getAgility());
		getStats().get(StatType.INTELLIGENCE).setBase(definition.getIntelligence());
		if (definition.getAbilitySet() == null) {
			definition.setAbilitySet(EMPTY_ABILITY_SET);
		}
		super.initialize();
	}

	@Override
	public void update() {
		super.update();
		if (getWorld().isServer()) {
			currentBehavior.update(this);
		}
	}

	public void move(double direction) {
		setMovingAngle(MoveUtils.avoidObstacles(this, direction, (int) OBSTACLE_VISION_DISTANCE, Math.PI / 4));
		setForward(true);
	}

	public void moveIfNotNull(Entity entity, double direction) {
		if (entity == null) {
			setForward(false);
		} else {
			move(direction);
		}
	}

	public void getAwayFrom(Entity target) {
		moveIfNotNull(target, target.angleToward(this));
	}

	public void moveToward(Entity target) {
		moveIfNotNull(target, this.angleToward(target));
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.CREATURE;
	}

	@Override
	public double getBoundingRadius() {
		return definition.getCollisionRadius();
	}

	@Override
	public AbilitySet getAbilitySet() {
		return definition.getAbilitySet();
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		super.writeUpdate(buffer);
		buffer.putShort((short) definition.getId());
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		super.applyUpdate(buffer);
		definition = getWorld().getContentPack().getCreatures().get(buffer.getShort());
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void foreach(TargetType targetType, Consumer<LivingEntity> action) {
		EntityPool entityPool = getWorld().getEntityPool();
		switch (targetType) {
		case ALL_ENEMIES:
			entityPool.get(EntityGroup.PLAYER).forEach((Consumer) action);
			break;
		case ALL_ALLIES:
			entityPool.get(EntityGroup.CREATURE).forEach((Consumer) action);
			break;
		case OTHER_ALLIES:
			for (Entity entity : entityPool.get(EntityGroup.CREATURE)) {
				if (!this.equals(entity)) {
					action.accept((LivingEntity) entity);
				}
			}
			break;
		}
	}
}
