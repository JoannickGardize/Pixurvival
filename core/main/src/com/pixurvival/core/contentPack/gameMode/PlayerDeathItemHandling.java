package com.pixurvival.core.contentPack.gameMode;

import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.PlayerEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

@AllArgsConstructor
public enum PlayerDeathItemHandling {
    DROP(p -> {
        for (int i = 0; i < Equipment.EQUIPMENT_SIZE; i++) {
            dropItemOnDeath(p, p.getEquipment().get(i));
        }
        for (int i = 0; i < p.getInventory().size(); i++) {
            dropItemOnDeath(p, p.getInventory().getSlot(i));
        }
        dropItemOnDeath(p, p.getInventory().getHeldItemStack());
        removeAll(p);
    }),
    KEEP(p -> {
        // Nothing to do
    }),
    REMOVE(p -> removeAll(p));

    private @Getter Consumer<PlayerEntity> handler;

    private static void dropItemOnDeath(PlayerEntity player, ItemStack itemStack) {
        if (itemStack != null) {
            ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
            player.getWorld().getEntityPool().addNew(itemStackEntity);
            itemStackEntity.getPosition().set(player.getPosition());
            itemStackEntity.spawnRandom();
        }
    }

    private static void removeAll(PlayerEntity player) {
        player.getInventory().removeAll();
        for (int i = 0; i < Equipment.EQUIPMENT_SIZE; i++) {
            player.getEquipment().set(i, null);
        }
    }
}
