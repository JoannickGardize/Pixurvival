package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.util.MoveUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class CreatureEntity extends LivingEntity {

	private static final AbilitySet EMPTY_ABILITY_SET = new AbilitySet();

	public static final double OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter Creature definition;

	public CreatureEntity(Creature definition) {
		this.definition = definition;
	}

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
		buffer.put((byte) definition.getId());
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		super.applyUpdate(buffer);
		definition = getWorld().getContentPack().getCreatures().get(buffer.get());
	}
}
