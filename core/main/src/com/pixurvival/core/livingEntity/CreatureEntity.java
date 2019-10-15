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
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.AlterationAbility;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.TeamMember;
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

	private static final AbilitySet<AlterationAbility> EMPTY_ABILITY_SET = new AbilitySet<>();

	public static final double OBSTACLE_VISION_DISTANCE = 4;

	private @Getter @Setter Behavior currentBehavior;
	private @Getter @Setter BehaviorData behaviorData;
	private @Getter @NonNull Creature definition;
	private @Getter @Setter Entity targetEntity;
	private @Getter Vector2 spawnPosition;
	private @Getter TeamMember master = this;

	@Override
	public void initialize() {
		if (definition.getAbilitySet() == null) {
			definition.setAbilitySet(EMPTY_ABILITY_SET);
		}
		// Add instead of setting base for the case that bonuses are applied at
		// creation.
		getStats().get(StatType.STRENGTH).addToBase(definition.getStrength());
		getStats().get(StatType.AGILITY).addToBase(definition.getAgility());
		getStats().get(StatType.INTELLIGENCE).addToBase(definition.getIntelligence());
		super.initialize();
		if (getWorld().isServer()) {
			behaviorData = new BehaviorData(this);
			currentBehavior = definition.getBehaviorSet().getBehaviors().get(0);
			currentBehavior.begin(this);
			spawnPosition = getPosition().copy();
		}
	}

	@Override
	public void update() {
		if (getWorld().isServer()) {
			currentBehavior.update(this);
			updateTargetPosition();
		}
		super.update();
	}

	@Override
	public void startAbility(int abilityId) {
		updateTargetPosition();
		super.startAbility(abilityId);
	}

	private void updateTargetPosition() {
		if (getWorld().isServer() && targetEntity != null) {
			getTargetPosition().set(targetEntity.getPosition());
		}
	}

	@Override
	protected void onDeath() {
		if (getWorld().isServer() && definition.getItemReward() != null) {
			ItemStack[] items = definition.getItemReward().produce(getWorld().getRandom());
			ItemStackEntity.spawn(getWorld(), items, getPosition());
		}
	}

	public void move(double direction, double forwardFactor) {
		setForwardFactor(forwardFactor);
		if (isSolid()) {
			setMovingAngle(MoveUtils.avoidObstacles(this, direction, (int) OBSTACLE_VISION_DISTANCE, Math.PI / 4));
		} else {
			setMovingAngle(direction);
		}
		setForward(true);
	}

	public void move(double direction) {
		move(direction, 1);
	}

	public void moveIfNotNull(Positionnable entity, double direction) {
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

	public void moveToward(Positionnable target, double randomAngle) {
		moveIfNotNull(target, this.angleToward(target) + (randomAngle == 0 ? 0 : getWorld().getRandom().nextAngle(randomAngle)));
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

	@Override
	public TeamMember getOrigin() {
		return master;
	}

	public void setMaster(TeamMember master) {
		this.master = master;
		setTeam(master.getTeam());
	}
}
