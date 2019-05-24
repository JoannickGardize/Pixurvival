package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.EffectAbility;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.util.MoveUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
public class CreatureEntity extends LivingEntity {

	private static final AbilitySet<EffectAbility> EMPTY_ABILITY_SET = new AbilitySet<>();

	public static final double OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter @NonNull Creature definition;
	private @Getter @Setter Entity targetEntity;
	private @Getter Vector2 anchorPosition;

	@Override
	public void initialize() {
		if (getWorld().isServer()) {
			currentBehavior = definition.getBehaviorSet().getBehaviors().get(0);
			currentBehavior.begin(this);
			anchorPosition = getPosition().copy();
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
		if (getWorld().isServer()) {
			currentBehavior.update(this);
			if (targetEntity != null) {
				getTargetPosition().set(targetEntity.getPosition());
			}
		}
		super.update();
	}

	@Override
	protected void onDeath() {
		if (getWorld().isServer() && definition.getItemReward() != null) {
			ItemStack[] items = definition.getItemReward().produce(getWorld().getRandom());
			ItemStackEntity.spawn(getWorld(), items, getPosition());
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
	public double getCollisionRadius() {
		return definition.getCollisionRadius();
	}

	@Override
	public AbilitySet<? extends Ability> getAbilitySet() {
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
}
