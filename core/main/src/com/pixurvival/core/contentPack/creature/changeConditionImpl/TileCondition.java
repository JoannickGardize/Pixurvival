package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import java.util.List;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.IdSetHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private List<Tile> tiles;

	private transient IdSetHelper idSetHelper = new IdSetHelper();

	@Override
	public boolean test(CreatureEntity creature) {
		return idSetHelper.get(tiles).contains(creature.getWorld().getMap().tileAt(creature.getPosition()).getTileDefinition().getId());
	}

}
