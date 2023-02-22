package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UseItemRequest implements IPlayerActionRequest {

    private int slotIndex;

    @Override
    public void apply(PlayerEntity player) {
        ItemStack itemStack;
        if (PlayerInventory.HELD_ITEM_STACK_INDEX == slotIndex) {
            itemStack = player.getInventory().getHeldItemStack();
        } else {
            itemStack = player.getInventory().getSlot(slotIndex);
        }
        if (itemStack != null) {
            if (itemStack.getItem() instanceof EdibleItem) {
                player.useItem((EdibleItem) itemStack.getItem(), slotIndex);
            } else if (itemStack.getItem() instanceof WeaponItem) {
                switchEquipment(player, itemStack, Equipment.WEAPON_INDEX);
            } else if (itemStack.getItem() instanceof ClothingItem) {
                switchEquipment(player, itemStack, Equipment.CLOTHING_INDEX);
            } else if (itemStack.getItem() instanceof AccessoryItem) {
                if (player.getEquipment().getAccessory1() == null) {
                    switchEquipment(player, itemStack, Equipment.ACCESSORY1_INDEX);
                } else if (player.getEquipment().getAccessory2() == null) {
                    switchEquipment(player, itemStack, Equipment.ACCESSORY2_INDEX);
                } else {
                    switchEquipment(player, itemStack, player.getNextAccessorySwitch());
                    swapNextAccessorySwitch(player);
                }
            }
        }
    }

    private void swapNextAccessorySwitch(PlayerEntity player) {
        if (player.getNextAccessorySwitch() == Equipment.ACCESSORY1_INDEX) {
            player.setNextAccessorySwitch(Equipment.ACCESSORY2_INDEX);
        } else {
            player.setNextAccessorySwitch(Equipment.ACCESSORY1_INDEX);
        }
    }

    @Override
    public boolean isClientPreapply() {
        return false;
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<UseItemRequest> {

        @Override
        public void write(Kryo kryo, Output output, UseItemRequest object) {
            output.writeShort(object.slotIndex);
        }

        @Override
        public UseItemRequest read(Kryo kryo, Input input, Class<UseItemRequest> type) {
            return new UseItemRequest(input.readShort());
        }
    }

    private void switchEquipment(PlayerEntity player, ItemStack itemStack, int equipmentIndex) {
        if (!Equipment.canEquip(equipmentIndex, itemStack)) {
            return;
        }
        ItemStack actualEquipment = player.getEquipment().get(equipmentIndex);
        player.getEquipment().set(equipmentIndex, itemStack);
        if (PlayerInventory.HELD_ITEM_STACK_INDEX == slotIndex) {
            player.getInventory().setHeldItemStack(actualEquipment);
        } else {
            player.getInventory().setSlot(slotIndex, actualEquipment);
        }

    }

}
