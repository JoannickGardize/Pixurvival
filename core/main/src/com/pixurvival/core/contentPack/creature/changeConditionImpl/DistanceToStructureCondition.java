package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.IdSetHelper;

// TODO ???
public class DistanceToStructureCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	private List<Structure> structures = new ArrayList<>();

	private FloatComparison comparison;

	private float targetDistance;

	private transient IdSetHelper idSetHelper = new IdSetHelper();

	@Override
	public boolean test(CreatureEntity creature) {
		return comparison.testPresence(creature.getWorld().getMap().findClosestStructure(creature.getPosition(), targetDistance, idSetHelper.get(structures)));
	}

}
