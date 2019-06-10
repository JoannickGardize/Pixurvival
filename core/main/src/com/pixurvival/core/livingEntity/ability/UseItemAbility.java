package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.livingEntity.LivingEntity;

public class UseItemAbility extends WorkAbility {

	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean start(LivingEntity entity) {
		//TODO
		super.start(entity);
		EdibleItem edibleItem = ((UseItemAbilityData) getAbilityData(entity)).getEdibleItem();
		if (edibleItem == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public AbilityData createAbilityData() {
		return new UseItemAbilityData();
	}
	
	@Override
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public void workFinished(LivingEntity entity) {
		// TODO Auto-generated method stub
	}

	

	

}
