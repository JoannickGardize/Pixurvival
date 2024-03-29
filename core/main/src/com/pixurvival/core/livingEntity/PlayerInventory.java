package com.pixurvival.core.livingEntity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import lombok.Getter;

import java.util.Objects;

public class PlayerInventory extends Inventory {

    public static final int HELD_ITEM_STACK_INDEX = -1;

    private @Getter ItemStack heldItemStack;

    public PlayerInventory(int size) {
        super(size);
    }

    @Override
    public void set(Inventory other) {
        super.set(other);
        if (other instanceof PlayerInventory) {
            setHeldItemStack(((PlayerInventory) other).heldItemStack);
        }
    }

    public void setHeldItemStack(ItemStack itemStack) {
        if (!Objects.equals(itemStack, heldItemStack)) {
            ItemStack previous = heldItemStack;
            heldItemStack = itemStack;
            notifySlotChanged(HELD_ITEM_STACK_INDEX, previous, itemStack);
        }
    }

    @Override
    public void removeAll() {
        setHeldItemStack(null);
        super.removeAll();
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerInventory> {

        @Override
        public void write(Kryo kryo, Output output, PlayerInventory object) {
            output.writeVarInt(object.slots.length, true);
            for (ItemStack itemStack : object.slots) {
                kryo.writeObjectOrNull(output, itemStack, ItemStack.class);
            }
            kryo.writeObjectOrNull(output, object.heldItemStack, ItemStack.class);
        }

        @Override
        public PlayerInventory read(Kryo kryo, Input input, Class<PlayerInventory> type) {
            int length = input.readVarInt(true);
            PlayerInventory inventory = new PlayerInventory(length);
            for (int i = 0; i < length; i++) {
                inventory.slots[i] = kryo.readObjectOrNull(input, ItemStack.class);
            }
            inventory.setHeldItemStack(kryo.readObjectOrNull(input, ItemStack.class));
            return inventory;
        }
    }
}
