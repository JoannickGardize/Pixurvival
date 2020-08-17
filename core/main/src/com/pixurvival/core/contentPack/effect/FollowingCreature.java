package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.alteration.StatFormula;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingCreature extends FollowingElement {

	private static final long serialVersionUID = 1L;

	private Creature creature;
	private boolean owned = true;
	private StatFormula strengthBonus = new StatFormula();
	private StatFormula agilityBonus = new StatFormula();
	private StatFormula intelligenceBonus = new StatFormula();

	@Override
	public void apply(TeamMember ancestor) {
		TeamMember origin = ancestor.getOrigin();
		CreatureEntity creatureEntity = new CreatureEntity(creature);
		if (owned) {
			creatureEntity.setMaster(origin);
		}
		StatSet creatureStats = creatureEntity.getStats();
		StatSet originStats = origin.getStats();
		creatureStats.get(StatType.STRENGTH).setBase(strengthBonus.getValue(originStats));
		creatureStats.get(StatType.AGILITY).setBase(agilityBonus.getValue(originStats));
		creatureStats.get(StatType.INTELLIGENCE).setBase(intelligenceBonus.getValue(originStats));
		creatureEntity.getPosition().set(ancestor.getPosition());
		ancestor.getWorld().getEntityPool().create(creatureEntity);
	}

}
