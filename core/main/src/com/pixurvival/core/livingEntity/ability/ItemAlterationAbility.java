package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAlterationAbility extends AlterationAbility {

    private static final long serialVersionUID = 1L;

    @Valid
    @Nullable
    private ItemStack ammunition;

    @Override
    public boolean canFire(LivingEntity entity) {
        return ammunition == null || removeAmmunition(entity);
    }

    private boolean removeAmmunition(LivingEntity entity) {
        if (entity instanceof InventoryHolder) {
            Inventory inventory = entity.getInventory();
            return inventory != null && inventory.remove(ammunition);
        }
        return true;
    }
}
