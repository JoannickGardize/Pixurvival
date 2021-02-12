package com.pixurvival.core.item;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.livingEntity.PlayerInventory;

import lombok.experimental.UtilityClass;

/**
 * Utility class that implements standard interaction of player with
 * inventories.
 * 
 * @author SharkHendrix
 *
 */
@UtilityClass
public class PlayerInventoryInteractions {

	/**
	 * Convenience method for
	 * {@link #interact(PlayerInventory, Inventory, int, boolean)}, with
	 * {@code interactionInventory = playerInventory}, which means that the player
	 * is interacting with its own inventory.
	 * 
	 * @param playerInventory
	 * @param slotIndex
	 * @param splitMode
	 */
	public static void interact(PlayerInventory playerInventory, int slotIndex, boolean splitMode) {
		interact(playerInventory, playerInventory, slotIndex, splitMode);
	}

	/**
	 * <p>
	 * Standard player interaction with inventories.
	 * <p>
	 * Two interaction modes according to {@code splitMode}:
	 * <ul>
	 * <li><b>Normal mode:</b> swap the held item stack with the item stack of the
	 * slot.
	 * <li><b>Split mode:</b>
	 * <ul>
	 * <li>If the held item stack and the slot item stack are of the same type (or
	 * the slot is empty), one quantity is put from the held item stack to the slot
	 * item stack.
	 * <li>If the held item stack is empty, and the slot item stack is not empty,
	 * half of the quantity (rounded up) is put from the slot item stack to the held
	 * item stack.
	 * <li>In any other situation, a normal interaction is performed.
	 * </ul>
	 * </ul>
	 * 
	 * @param playerInventory
	 *            the player inventory. Only
	 *            {@link PlayerInventory#getHeldItemStack()} may change
	 * @param interactionInventory
	 *            the interaction inventory, only the slot at {@code slotIndex} may
	 *            change
	 * @param slotIndex
	 *            the slot index of the {@code interactionInventory} the player is
	 *            interacting with. If the index is invalid, nothing happens
	 * @param splitMode
	 *            if false, normal interaction is done, if true, a split interaction
	 *            is done
	 */
	public static void interact(PlayerInventory playerInventory, Inventory interactionInventory, int slotIndex, boolean splitMode) {
		if (!interactionInventory.isValidIndex(slotIndex)) {
			Log.warn("Warning : invalid slot index : " + slotIndex);
			return;
		}
		if (splitMode) {
			performSplitInventoryAction(playerInventory, interactionInventory, slotIndex);
		} else {
			performNormalInventoryAction(playerInventory, interactionInventory, slotIndex);
		}
	}

	public static void fill(Inventory source, Inventory destination) {
		for (int i = 0; i < source.size(); i++) {
			ItemStack itemStack = source.getSlot(i);
			if (itemStack != null) {
				ItemStack rest = destination.add(itemStack);
				source.setSlot(i, rest);
			}
		}
	}

	private static void performNormalInventoryAction(PlayerInventory playerInventory, Inventory interactionInventory, int slotIndex) {
		ItemStack currentContent = interactionInventory.getSlot(slotIndex);
		ItemStack heldItemStack = playerInventory.getHeldItemStack();
		if (heldItemStack != null && currentContent != null && heldItemStack.getItem() == currentContent.getItem()) {
			int quantity = currentContent.getQuantity();
			int overflow = currentContent.overflowingQuantity(heldItemStack.getQuantity());
			interactionInventory.setSlot(slotIndex, currentContent.copy(quantity + heldItemStack.getQuantity() - overflow));
			if (overflow == 0) {
				playerInventory.setHeldItemStack(null);
			} else {
				playerInventory.setHeldItemStack(heldItemStack.copy(overflow));
			}
		} else {
			interactionInventory.setSlot(slotIndex, heldItemStack);
			playerInventory.setHeldItemStack(currentContent);
		}
	}

	private static void performSplitInventoryAction(PlayerInventory playerInventory, Inventory interactionInventory, int slotIndex) {
		ItemStack currentContent = interactionInventory.getSlot(slotIndex);
		ItemStack heldItemStack = playerInventory.getHeldItemStack();
		if (heldItemStack != null && currentContent != null && heldItemStack.getItem() == currentContent.getItem()) {
			performStackOne(playerInventory, interactionInventory, slotIndex);
		} else if (heldItemStack != null && currentContent == null) {
			performPutOne(playerInventory, interactionInventory, slotIndex);
		} else if (heldItemStack == null && currentContent != null) {
			performTakeHalf(playerInventory, interactionInventory, slotIndex);
		} else {
			performNormalInventoryAction(playerInventory, interactionInventory, slotIndex);
		}
	}

	private static void performStackOne(PlayerInventory playerInventory, Inventory interactionInventory, int slotIndex) {
		ItemStack currentContent = interactionInventory.getSlot(slotIndex);
		ItemStack heldItemStack = playerInventory.getHeldItemStack();
		if (currentContent.overflowingQuantity(1) == 0) {
			interactionInventory.setSlot(slotIndex, currentContent.add(1));
			if (heldItemStack.getQuantity() == 1) {
				playerInventory.setHeldItemStack(null);
			} else {
				playerInventory.setHeldItemStack(heldItemStack.sub(1));
			}
		}
	}

	private static void performPutOne(PlayerInventory playerInventory, Inventory interactionInventory, int slotIndex) {
		ItemStack heldItemStack = playerInventory.getHeldItemStack();
		interactionInventory.setSlot(slotIndex, new ItemStack(heldItemStack.getItem()));
		if (heldItemStack.getQuantity() == 1) {
			playerInventory.setHeldItemStack(null);
		} else {
			playerInventory.setHeldItemStack(heldItemStack.sub(1));
		}
	}

	private static void performTakeHalf(PlayerInventory playerInventory, Inventory interactionInventory, int slotIndex) {
		ItemStack currentContent = interactionInventory.getSlot(slotIndex);
		int halfQuantity = currentContent.getQuantity() / 2 + (currentContent.getQuantity() % 2 == 0 ? 0 : 1);
		playerInventory.setHeldItemStack(new ItemStack(currentContent.getItem(), halfQuantity));
		if (currentContent.getQuantity() == halfQuantity) {
			interactionInventory.setSlot(slotIndex, null);
		} else {
			interactionInventory.setSlot(slotIndex, currentContent.sub(halfQuantity));
		}
	}
}
