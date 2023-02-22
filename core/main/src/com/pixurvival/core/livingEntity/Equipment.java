package com.pixurvival.core.livingEntity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.ClothingItem;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Equipment {

    public static final int WEAPON_INDEX = 0;
    public static final int CLOTHING_INDEX = 1;
    public static final int ACCESSORY1_INDEX = 2;
    public static final int ACCESSORY2_INDEX = 3;

    public static final int EQUIPMENT_SIZE = 4;

    private ItemStack[] equipment = new ItemStack[EQUIPMENT_SIZE];

    private List<EquipmentListener> listeners = new ArrayList<>();

    public void addListener(EquipmentListener listener) {
        listeners.add(listener);
    }

    public void set(Equipment other) {
        for (int i = 0; i < EQUIPMENT_SIZE; i++) {
            set(i, other.get(i));
        }
    }

    public void set(int index, ItemStack itemStack) {
        ItemStack previous = equipment[index];
        if (!Objects.equals(itemStack, previous)) {
            equipment[index] = itemStack;
            listeners.forEach(l -> l.equipmentChanged(this, index, previous, itemStack));
        }
    }

    public ItemStack get(int index) {
        return equipment[index];
    }

    public void setWeapon(ItemStack itemStack) {
        set(WEAPON_INDEX, itemStack);
    }

    public void setClothing(ItemStack itemStack) {
        set(CLOTHING_INDEX, itemStack);
    }

    public void setAccessory1(ItemStack itemStack) {
        set(ACCESSORY1_INDEX, itemStack);
    }

    public void setAccessory2(ItemStack itemStack) {
        set(ACCESSORY2_INDEX, itemStack);
    }

    public ItemStack getWeapon() {
        return equipment[WEAPON_INDEX];
    }

    public ItemStack getClothing() {
        return equipment[CLOTHING_INDEX];
    }

    public ItemStack getAccessory1() {
        return equipment[ACCESSORY1_INDEX];
    }

    public ItemStack getAccessory2() {
        return equipment[ACCESSORY2_INDEX];
    }

    public static boolean canEquip(int index, ItemStack itemStack) {
        if (itemStack.getQuantity() != 1) {
            return false;
        }
        switch (index) {
            case WEAPON_INDEX:
                return itemStack.getItem() instanceof WeaponItem;
            case CLOTHING_INDEX:
                return itemStack.getItem() instanceof ClothingItem;
            case ACCESSORY1_INDEX:
                return itemStack.getItem() instanceof AccessoryItem;
            case ACCESSORY2_INDEX:
                return itemStack.getItem() instanceof AccessoryItem;
            default:
                return false;
        }
    }

    public static boolean canEquipAnywhere(ItemStack itemStack) {
        return itemStack.getQuantity() == 1 && itemStack.getItem() instanceof EquipableItem;
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<Equipment> {

        @Override
        public void write(Kryo kryo, Output output, Equipment object) {
            for (int i = 0; i < EQUIPMENT_SIZE; i++) {
                kryo.writeObjectOrNull(output, object.equipment[i], ItemStack.class);
            }
        }

        @Override
        public Equipment read(Kryo kryo, Input input, Class<Equipment> type) {
            Equipment equipment = new Equipment();
            for (int i = 0; i < EQUIPMENT_SIZE; i++) {
                equipment.equipment[i] = kryo.readObjectOrNull(input, ItemStack.class);
            }
            return equipment;
        }
    }
}
