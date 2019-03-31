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

	public static final double OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter Creature definition;

	public CreatureEntity(Creature definition) {
		this.definition = definition;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isServer()) {
			currentBehavior = definition.getBehaviorSet().getBehaviors().get(0);
			currentBehavior.begin(this);
		}
	}

	@Override
	public void update() {
		super.update();
		if (getWorld().isServer()) {
			currentBehavior.update(this);
		}
	}

	public void move(double direction) {
		MoveUtils.avoidObstacles(this, direction, (int) OBSTACLE_VISION_DISTANCE, Math.PI / 4);
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
	public void writeUpdate(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public AbilitySet getAbilitySet() {
		return definition.getAbilitySet();
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}
