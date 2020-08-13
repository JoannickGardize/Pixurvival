package com.pixurvival.core.livingEntity;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class KillCreatureEntityAction implements Action {

	long id;

	@Override
	public void perform(World world) {
		Entity e = world.getEntityPool().get(EntityGroup.CREATURE, id);
		if (e != null) {
			e.setAlive(false);
		}
	}

}
