package com.pixurvival.core.item;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.util.VarLenNumberIO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

// TODO abstract item collection
@EqualsAndHashCode(of = "slots")
public class Inventory {

    protected ItemStack[] slots;
    private int[] quantities;
    private List<InventoryListener> listeners = new ArrayList<>();

    public Inventory(int size) {
        slots = new ItemStack[size];
    }

    public void set(Inventory other) {
        for (int i = 0; i < slots.length; i++) {
            setSlot(i, other.getSlot(i));
        }
    }

    public void addListener(InventoryListener listener) {
        listeners.add(listener);
    }

    public void removeListener(InventoryListener listener) {
        listeners.remove(listener);
    }

    public int size() {
        return slots.length;
    }

    /**
     * Set the given slot to the given itemStack.
     *
     * @param index
     * @param itemStack
     */
    public void setSlot(int index, ItemStack itemStack) {
        if (!Objects.equals(itemStack, slots[index])) {
            ItemStack previousItemStack = slots[index];
            slots[index] = itemStack;
            slotChanged(index, previousItemStack, itemStack);
        }
    }

    public ItemStack getSlot(int index) {
        return slots[index];
    }

    public ItemStack take(int index) {
        ItemStack itemStack = getSlot(index);
        setSlot(index, null);
        return itemStack;
    }

    /**
     * @param itemStacks each item stack must have a different item type
     * @return
     */
    public boolean contains(Collection<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (totalOf(itemStack.getItem()) < itemStack.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(ItemStack itemStack) {
        return totalOf(itemStack.getItem()) >= itemStack.getQuantity();
    }

    public boolean contains(Item item, int quantity) {
        return totalOf(item) >= quantity;
    }

    public int totalOf(Item item) {
        if (quantities == null || quantities.length <= item.getId()) {
            return 0;
        } else {
            return quantities[item.getId()];
        }
    }

    /**
     * Try to take the given collection of item with the given quantity. If the
     * quantity is not available nothing happen. The items are taken in priority
     * from the end.
     *
     * @param itemStacks The collection of ItemStack to remove
     * @return True if the ItemStacks were available and has been removed, false
     * overwise.
     */
    public boolean remove(Collection<ItemStack> itemStacks) {
        if (!contains(itemStacks)) {
            return false;
        }
        unsafeRemove(itemStacks);
        return true;
    }

    /**
     * Try to take the given item with the given quantity. If the quantity is not
     * available nothing happen. The items are taken in priority from the end.
     *
     * @param itemStack The ItemStack to remove
     * @return True if the ItemStack were available and has been removed, false
     * overwise.
     */
    public boolean remove(ItemStack itemStack) {
        if (!contains(itemStack)) {
            return false;
        }
        unsafeRemove(itemStack);
        return true;
    }

    /**
     * Try to take the given item with the given quantity. If the quantity is not
     * available nothing happen. The items are taken in priority from the end.
     *
     * @param item
     * @param quantity
     * @return true if the quantity was successfully removed
     */
    public boolean remove(Item item, int quantity) {
        if (!contains(item, quantity)) {
            return false;
        }
        unsafeRemove(item, quantity);
        return true;
    }

    /**
     * Remove all possible items from the given collection. A call to
     * {@link #contains(Collection)} before this method is recommended to make sure
     * all the quantity will be removed.
     *
     * @param itemStacks
     */
    public void unsafeRemove(Collection<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            unsafeRemove(itemStack);
        }
    }

    public boolean unsafeRemove(ItemStack itemStack) {
        return unsafeRemove(itemStack.getItem(), itemStack.getQuantity());
    }

    /**
     * Remove the maximum of quantity of the given item, in the limit of the
     * maximumQuantity parameter. Used after a call to
     * {@link Inventory#contains(ItemStack)}.
     *
     * @param item
     * @param maximumQuantity
     * @return
     */
    public boolean unsafeRemove(Item item, int maximumQuantity) {
        int remainingQuantity = maximumQuantity;
        for (int i = slots.length - 1; i >= 0; i--) {
            ItemStack slot = slots[i];
            if (slot != null && slot.getItem() == item) {
                if (slot.getQuantity() > remainingQuantity) {
                    setSlot(i, slot.sub(remainingQuantity));
                    return true;
                } else {
                    setSlot(i, null);
                    if (slot.getQuantity() == remainingQuantity) {
                        return true;
                    } else {
                        remainingQuantity -= slot.getQuantity();
                    }
                }
            }
        }
        return false;
    }

    public void removeAll() {
        for (int i = slots.length - 1; i >= 0; i--) {
            setSlot(i, null);
        }
    }

    /**
     * Try to add the maximum quantity of the given ItemStack to this inventory, it
     * will be stacked in priority with similar items if possible. It can be split
     * into different slots if necessary.
     *
     * @param itemStack The ItemStack to add.
     * @return An ItemStack containing the quantity that cannot be added to this
     * inventory, or null if all the quantity was added.
     */
    public ItemStack add(ItemStack itemStack) {
        Item item = itemStack.getItem();
        int remainingQuantity = itemStack.getQuantity();
        for (int i = 0; i < slots.length; i++) {
            ItemStack slot = slots[i];
            if (slot != null && slot.getItem() == item) {
                int overflow = slot.overflowingQuantity(remainingQuantity);
                setSlot(i, slot.add(remainingQuantity - overflow));
                if (overflow == 0) {
                    return null;
                }
                remainingQuantity = overflow;
            }
        }
        int emptySlot = 0;
        while ((emptySlot = findEmptySlot(emptySlot)) != -1) {
            int maxQuantity = Math.min(remainingQuantity, itemStack.getItem().getMaxStackSize());
            setSlot(emptySlot, itemStack.copy(maxQuantity));
            remainingQuantity -= maxQuantity;
            if (remainingQuantity <= 0) {
                return null;
            }
        }
        return itemStack.copy(remainingQuantity);
    }

    @Getter
    @AllArgsConstructor
    private static class SetSlotMemory {
        int index;
        ItemStack itemStack;
    }

    /**
     * <p>
     * Add all ItemStacks in parameters if possible. Return false if this is not
     * possible. It will never add a part of the collection. Items are added with
     * the same behavior of {@link #add(ItemStack)}.
     * <p>
     * The parameter must contains only one entry per item type, otherwise the
     * behavior of this method is undetermined.
     *
     * @param itemStacks the item stacks to add together in this inventory. It must
     *                   contains only one entry per item type
     * @return true if the items has been added, false otherwise
     */
    public boolean addAllOrFail(Collection<ItemStack> itemStacks) {
        List<SetSlotMemory> setSlotMemoryList = new ArrayList<>();
        int emptySlotIndex = 0;
        for (ItemStack itemStack : itemStacks) {
            emptySlotIndex = addLater(itemStack, emptySlotIndex, setSlotMemoryList);
            if (emptySlotIndex == -1) {
                return false;
            }
        }
        for (SetSlotMemory setSlotMemory : setSlotMemoryList) {
            setSlot(setSlotMemory.getIndex(), setSlotMemory.getItemStack());
        }
        return true;
    }

    private int addLater(ItemStack itemStack, int emptySlotBeginIndex, List<SetSlotMemory> setSlotMemoryList) {
        Item item = itemStack.getItem();
        int remainingQuantity = itemStack.getQuantity();
        for (int i = 0; i < slots.length; i++) {
            ItemStack slot = slots[i];
            if (slot != null && slot.getItem() == item) {
                int overflow = slot.overflowingQuantity(remainingQuantity);
                setSlotMemoryList.add(new SetSlotMemory(i, slot.add(remainingQuantity - overflow)));
                if (overflow == 0) {
                    return emptySlotBeginIndex;
                }
                remainingQuantity = overflow;
            }
        }
        int emptySlot = emptySlotBeginIndex;
        while ((emptySlot = findEmptySlot(emptySlot)) != -1) {
            int maxQuantity = Math.min(remainingQuantity, itemStack.getItem().getMaxStackSize());
            setSlotMemoryList.add(new SetSlotMemory(emptySlot, itemStack.copy(maxQuantity)));
            remainingQuantity -= maxQuantity;
            if (remainingQuantity <= 0) {
                return emptySlot + 1;
            }
        }
        return -1;
    }

    public void foreachItemStacks(Consumer<ItemStack> action) {
        for (int i = 0; i < slots.length; i++) {
            ItemStack slot = slots[i];
            if (slot != null) {
                action.accept(slot);
            }
        }
    }

    public int findEmptySlot() {
        return findEmptySlot(0);
    }

    public int findEmptySlot(int startIndex) {
        for (int i = startIndex; i < slots.length; i++) {
            if (slots[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int findNotEmptySlot(int startIndex) {
        for (int i = startIndex; i < slots.length; i++) {
            if (slots[i] != null) {
                return i;
            }
        }
        return -1;
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < slots.length;
    }

    public void slotChanged(int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {

        if (previousItemStack == null) {
            ensureQuantitiesArrayLength(newItemStack.getItem().getId());
            quantities[newItemStack.getItem().getId()] += newItemStack.getQuantity();
        } else if (newItemStack == null) {
            ensureQuantitiesArrayLength(previousItemStack.getItem().getId());
            quantities[previousItemStack.getItem().getId()] -= previousItemStack.getQuantity();
        } else if (previousItemStack.getItem() == newItemStack.getItem()) {
            ensureQuantitiesArrayLength(previousItemStack.getItem().getId());
            quantities[newItemStack.getItem().getId()] += newItemStack.getQuantity() - previousItemStack.getQuantity();
        } else {
            ensureQuantitiesArrayLength(Math.max(newItemStack.getItem().getId(), previousItemStack.getItem().getId()));
            quantities[previousItemStack.getItem().getId()] -= previousItemStack.getQuantity();
            quantities[newItemStack.getItem().getId()] += newItemStack.getQuantity();
        }
        notifySlotChanged(slotIndex, previousItemStack, newItemStack);
    }

    public void computeQuantities() {
        ensureQuantitiesArrayLength(0);
        Arrays.fill(quantities, 0);
        for (ItemStack itemStack : slots) {
            if (itemStack != null) {
                ensureQuantitiesArrayLength(itemStack.getItem().getId());
                quantities[itemStack.getItem().getId()] += itemStack.getQuantity();
            }
        }
    }

    /**
     * Small compression topic here, by counting successive empty slots.
     *
     * @param buffer
     */
    public void write(ByteBuffer buffer) {
        int emptyAntiCount = 0;
        for (ItemStack itemStack : slots) {
            if (itemStack == null) {
                emptyAntiCount--;
            } else {
                if (emptyAntiCount < 0) {
                    VarLenNumberIO.writeVarInt(buffer, emptyAntiCount);
                    emptyAntiCount = 0;
                }
                VarLenNumberIO.writeVarInt(buffer, itemStack.getItem().getId());
                VarLenNumberIO.writePositiveVarInt(buffer, itemStack.getQuantity());
            }
        }
        if (emptyAntiCount < 0) {
            VarLenNumberIO.writeVarInt(buffer, emptyAntiCount);
        }
    }

    public void apply(World world, ByteBuffer buffer) {
        apply(world.getContentPack(), buffer);
    }

    public void apply(ContentPack contentPack, ByteBuffer buffer) {
        int i = 0;
        while (i < size()) {
            int id = VarLenNumberIO.readVarInt(buffer);
            if (id < 0) {
                for (id = i - id; i < id; i++) {
                    setSlot(i, null);
                }
            } else {
                setSlot(i, new ItemStack(contentPack.getItems().get(id), VarLenNumberIO.readPositiveVarInt(buffer)));
                i++;
            }
        }
    }

    protected void notifySlotChanged(int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
        listeners.forEach(l -> l.slotChanged(this, slotIndex, previousItemStack, newItemStack));
    }

    private void ensureQuantitiesArrayLength(int maxIndex) {
        if (quantities == null) {
            quantities = new int[maxIndex + 5];
        } else if (quantities.length <= maxIndex) {
            int[] newArray = new int[maxIndex + 5];
            System.arraycopy(quantities, 0, newArray, 0, quantities.length);
            quantities = newArray;
        }
    }

    public static class Serializer extends com.esotericsoftware.kryo.Serializer<Inventory> {

        @Override
        public void write(Kryo kryo, Output output, Inventory object) {
            output.writeVarInt(object.slots.length, true);
            for (ItemStack itemStack : object.slots) {
                kryo.writeObjectOrNull(output, itemStack, ItemStack.class);
            }
        }

        @Override
        public Inventory read(Kryo kryo, Input input, Class<Inventory> type) {
            int length = input.readVarInt(true);
            Inventory inventory = new Inventory(length);
            for (int i = 0; i < length; i++) {
                inventory.slots[i] = kryo.readObjectOrNull(input, ItemStack.class);
            }
            return inventory;
        }
    }

}
