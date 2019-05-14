package com.pixurvival.core.livingEntity.ability;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.alteration.Alteration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectAbility extends CooldownAbility {

	private static final long serialVersionUID = 1L;

	private List<Alteration> selfAlterations;
	private List<Effect> effects;
	private Item ammunition;

	private transient Collection<ItemStack> itemCollection;

	@Override
	public void fire(LivingEntity entity) {
		if (entity.getWorld().isServer()) {
			if (!removeAmmunition(entity)) {
				return;
			}
			if (selfAlterations != null) {
				selfAlterations.forEach(a -> a.apply(this, entity));
			}
			for (Effect effect : effects) {
				EffectEntity effectEntity = new EffectEntity(effect, entity);
				entity.getWorld().getEntityPool().add(effectEntity);
			}
		}
	}

	private boolean removeAmmunition(LivingEntity entity) {
		if (ammunition != null && entity instanceof InventoryHolder) {
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			return inventory.remove(getItemCollection());
		}
		return true;
	}

	private Collection<ItemStack> getItemCollection() {
		if (itemCollection == null) {
			itemCollection = Collections.singleton(new ItemStack(ammunition));
		}
		return itemCollection;
	}
}