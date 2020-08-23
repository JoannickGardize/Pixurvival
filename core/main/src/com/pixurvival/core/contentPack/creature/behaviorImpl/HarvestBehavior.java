package com.pixurvival.core.contentPack.creature.behaviorImpl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.structure.HarvestableStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.map.HarvestableMapStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HarvestBehavior extends Behavior {

	public static final float MOVE_TOWARD_TOLERANCE = 0.1f;

	private static final long serialVersionUID = 1L;

	private static final Map<World, Set<HarvestableMapStructure>> BOOKED_STRUCTURES = new WeakHashMap<>();

	private float searchDistance = 10;

	private ElementSet<Structure> structures = new AllElementSet<>();

	@Override
	protected void step(CreatureEntity creature) {
		HarvestableMapStructure currentStructureTarget = (HarvestableMapStructure) creature.getBehaviorData().getCustomData();
		if (creature.getCurrentAbility() instanceof HarvestAbility) {
			if (!failIfCurrentStructureTargetIsInvalid(creature, currentStructureTarget)) {
				setHarvestStandby(creature, currentStructureTarget);
			}
			return;
		}
		if (currentStructureTarget == null) {
			currentStructureTarget = searchStructureToHarvest(creature);
			if (currentStructureTarget == null) {
				stop(creature);
				creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
				creature.getBehaviorData().setNothingToDo(true);
				return;
			}
		} else if (currentStructureTarget.isHarvested()
				&& creature.distanceSquared(currentStructureTarget) < GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE * GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE) {
			creature.getBehaviorData().setTaskFinished(true);
			stop(creature);
			creature.getBehaviorData().setNextUpdateDelayMillis(0);
			return;
		} else if (failIfCurrentStructureTargetIsInvalid(creature, currentStructureTarget)) {
			return;
		}
		creature.getTargetPosition().set(currentStructureTarget.getPosition());
		float distanceSquared = creature.distanceSquared(currentStructureTarget);
		if (distanceSquared < GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE * GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE) {
			creature.setForward(false);
			creature.harvest(currentStructureTarget);
			setHarvestStandby(creature, currentStructureTarget);
		} else {
			float distance = (float) Math.sqrt(distanceSquared);
			creature.moveTowardPrecisely(currentStructureTarget, distance);
			creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(distance - GameConstants.MAX_STRUCTURE_INTERACTION_DISTANCE + MOVE_TOWARD_TOLERANCE);
		}
	}

	@Override
	protected void end(CreatureEntity creature) {
		updateTargetReference(creature, null);
	}

	private HarvestableMapStructure searchStructureToHarvest(CreatureEntity creature) {
		HarvestableMapStructure currentStructureTarget = (HarvestableMapStructure) creature.getWorld().getMap().findClosestStructure(creature.getPosition(), searchDistance,
				s -> structures.containsById(s), s -> s instanceof HarvestableMapStructure && !((HarvestableMapStructure) s).isHarvested() && !getBookedStructures(creature).contains(s));
		updateTargetReference(creature, currentStructureTarget);
		return currentStructureTarget;
	}

	private boolean failIfCurrentStructureTargetIsInvalid(CreatureEntity creature, HarvestableMapStructure currentStructureTarget) {
		if (currentStructureTarget.isHarvested() || !currentStructureTarget.equals(currentStructureTarget.getWorld().getMap().tileAt(currentStructureTarget.getPosition()).getStructure())) {
			stop(creature);
			creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
			return true;
		} else {
			return false;
		}
	}

	private void setHarvestStandby(CreatureEntity creature, HarvestableMapStructure currentStructureTarget) {
		creature.getBehaviorData().setNextUpdateDelayMillis(Math.min(((HarvestableStructure) currentStructureTarget.getDefinition()).getHarvestingTime(), BehaviorData.DEFAULT_STANDBY_DELAY));
	}

	private void stop(CreatureEntity creature) {
		creature.setForward(false);
		creature.stopCurrentAbility();
		updateTargetReference(creature, null);
	}

	private void updateTargetReference(CreatureEntity creature, HarvestableMapStructure target) {
		if (target == null) {
			getBookedStructures(creature).remove(creature.getBehaviorData().getCustomData());
		} else {
			getBookedStructures(creature).add(target);
		}
		creature.getBehaviorData().setCustomData(target);
	}

	private Set<HarvestableMapStructure> getBookedStructures(CreatureEntity creature) {
		return BOOKED_STRUCTURES.computeIfAbsent(creature.getWorld(), w -> new HashSet<>());
	}
}