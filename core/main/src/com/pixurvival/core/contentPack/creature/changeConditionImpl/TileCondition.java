package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@Valid
	private ElementSet<Tile> tileSet = new AllElementSet<>();

	@Override
	public boolean test(CreatureEntity creature) {
		return tileSet.contains(creature.getWorld().getMap().tileAt(creature.getPosition()).getTileDefinition());
	}

}
