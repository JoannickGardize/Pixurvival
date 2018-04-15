package com.pixurvival.core.aliveEntity;

import java.nio.ByteBuffer;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.message.CraftItemRequest;
import com.pixurvival.core.message.DropItemRequest;
import com.pixurvival.core.message.InteractStructureRequest;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.PlayerActionRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends AliveEntity implements InventoryHolder {

	private @Setter Activity activity = Activity.NONE;

	private String name;

	private @Setter PlayerInventory inventory;

	@Setter
	private Position chunkPosition;

	private StatSet stats = new StatSet();

	public PlayerEntity() {
	}

	public void apply(PlayerActionRequest actionRequest) {
		setMovingAngle(actionRequest.getDirection().getAngle());
		setForward(actionRequest.isForward());
	}

	public void apply(CraftItemRequest request) {
		ItemCraft craft = getWorld().getContentPack().getItemCraftsById().get(request.getCraftId());
		if (inventory.contains(craft.getRecipes()) && activity.in(Activity.NONE_ID, Activity.CRAFTING_ACTIVITY_ID)) {
			activity = new CraftingActivity(this, craft);
		}
	}

	public void apply(InventoryActionRequest actionRequest) {
		if (!inventory.isValidIndex(actionRequest.getSlotIndex())) {
			Log.warn("Warning : invalid slot index : " + actionRequest.getSlotIndex());
			return;
		}
		switch (actionRequest.getType()) {
		case SWAP_CLICK_MY_INVENTORY:
			performNormalInventoryAction(actionRequest.getSlotIndex());
			break;
		case SPLIT_CLICK_MY_INVENTORY:
			ItemStack currentContent = inventory.getSlot(actionRequest.getSlotIndex());
			ItemStack heldItemStack = inventory.getHeldItemStack();
			if (heldItemStack != null && currentContent != null
					&& heldItemStack.getItem() == currentContent.getItem()) {
				if (currentContent.overflowingQuantity(1) == 0) {
					inventory.setSlot(actionRequest.getSlotIndex(), currentContent.add(1));
					if (heldItemStack.getQuantity() == 1) {
						inventory.setHeldItemStack(null);
					} else {
						inventory.setHeldItemStack(heldItemStack.sub(1));
					}
				}
			} else if (heldItemStack != null && currentContent == null) {
				inventory.setSlot(actionRequest.getSlotIndex(), new ItemStack(heldItemStack.getItem()));
				if (heldItemStack.getQuantity() == 1) {
					inventory.setHeldItemStack(null);
				} else {
					inventory.setHeldItemStack(heldItemStack.sub(1));
				}
			} else if (heldItemStack == null && currentContent != null) {
				int halfQuantity = currentContent.getQuantity() / 2 + (currentContent.getQuantity() % 2 == 0 ? 0 : 1);
				inventory.setHeldItemStack(new ItemStack(currentContent.getItem(), halfQuantity));
				if (currentContent.getQuantity() == halfQuantity) {
					inventory.setSlot(actionRequest.getSlotIndex(), null);
				} else {
					inventory.setSlot(actionRequest.getSlotIndex(), currentContent.sub(halfQuantity));
				}
			} else {
				performNormalInventoryAction(actionRequest.getSlotIndex());
			}
			break;
		}
	}

	public void apply(InteractStructureRequest request) {
		MapTile mapTile = getWorld().getMap().tileAt(request.getX(), request.getY());
		if (mapTile.getStructure() instanceof HarvestableStructure && mapTile.getStructure().canInteract(this)) {
			HarvestableStructure structure = (HarvestableStructure) mapTile.getStructure();
			activity = new HarvestingActivity(this, structure);
			setForward(false);
		}
	}

	public void apply(DropItemRequest request) {
		if (getInventory().getHeldItemStack() != null) {
			ItemStackEntity entity = new ItemStackEntity(getInventory().getHeldItemStack());
			entity.getPosition().set(getPosition());
			getWorld().getEntityPool().add(entity);
			entity.spawn(request.getDirection());
			getInventory().setHeldItemStack(null);
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isServer()) {
			inventory = new PlayerInventory(getInventorySize());
		}
		stats.get(StatType.MAX_HEALTH).addListener(s -> {
			if (getHealth() > s.getValue()) {
				setHealth(s.getValue());
			}
		});
	}

	@Override
	public void update() {
		super.update();

		if (isForward() && !activity.canMove()) {
			activity = Activity.NONE;
		}
		activity.update();
	}

	@Override
	public double getMaxHealth() {
		return stats.valueOf(StatType.MAX_HEALTH);
	}

	@Override
	public double getSpeedPotential() {
		return stats.valueOf(StatType.SPEED)
				* getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor();
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.PLAYER;
	}

	@Override
	public double getBoundingRadius() {
		return 0.42;
	}

	public int getInventorySize() {
		return 32;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		// normal part
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		buffer.putDouble(getMovingAngle());
		buffer.put(isForward() ? (byte) 1 : (byte) 0);
		buffer.putDouble(getHealth());
		buffer.putDouble(getAimingAngle());

		buffer.put(getActivity().getId());
		switch (getActivity().getId()) {
		case Activity.HARVESTING_ID:
			HarvestingActivity harvestingActivity = (HarvestingActivity) getActivity();
			buffer.putInt(harvestingActivity.getStructure().getTileX());
			buffer.putInt(harvestingActivity.getStructure().getTileY());
			buffer.putDouble(harvestingActivity.getProgressTime());
			break;
		case Activity.CRAFTING_ACTIVITY_ID:
			CraftingActivity craftingActivity = (CraftingActivity) getActivity();
			buffer.putShort(craftingActivity.getItemCraft().getId());
			buffer.putDouble(craftingActivity.getProgress());
			break;
		}
		// extended part
		// if (extendedUpdateRequired) {
		// buffer.put((byte) 1);
		// } else {
		// buffer.put((byte) 0);
		// }
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		// normal part
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setMovingAngle(buffer.getDouble());
		setForward(buffer.get() == 1 ? true : false);
		setHealth(buffer.getDouble());
		setAimingAngle(buffer.getDouble());
		byte activityId = buffer.get();
		switch (activityId) {
		case Activity.NONE_ID:
			activity = Activity.NONE;
			break;
		case Activity.HARVESTING_ID:
			int tileX = buffer.getInt();
			int tileY = buffer.getInt();
			double progressTime = buffer.getDouble();
			if (!(getActivity() instanceof HarvestingActivity)) {
				MapTile tile = getWorld().getMap().tileAt(tileX, tileY);
				if (tile.getStructure() instanceof HarvestableStructure) {
					HarvestingActivity harvestingActivity = new HarvestingActivity(this,
							(HarvestableStructure) tile.getStructure());
					harvestingActivity.setProgressTime(progressTime);
					activity = harvestingActivity;
				} else {
					Log.warn("Unknown harvesting tile");
				}
			}
			break;
		case Activity.CRAFTING_ACTIVITY_ID:
			short craftId = buffer.getShort();
			progressTime = buffer.getDouble();
			if (!(getActivity() instanceof CraftingActivity)) {
				CraftingActivity activity = new CraftingActivity(this,
						getWorld().getContentPack().getItemCraftsById().get(craftId));
				setActivity(activity);
				activity.setProgressTime(progressTime);
			} else {
				((CraftingActivity) getActivity()).setProgressTime(progressTime);
			}
			break;
		}

		// extended part
		// if (buffer.get() == 1) {
		// }
	}

	private void performNormalInventoryAction(int slotIndex) {
		ItemStack currentContent = inventory.getSlot(slotIndex);
		ItemStack heldItemStack = inventory.getHeldItemStack();
		if (heldItemStack != null && currentContent != null && heldItemStack.getItem() == currentContent.getItem()) {
			int quantity = currentContent.getQuantity();
			int overflow = currentContent.overflowingQuantity(heldItemStack.getQuantity());
			inventory.setSlot(slotIndex, currentContent.copy(quantity + heldItemStack.getQuantity() - overflow));
			if (overflow == 0) {
				inventory.setHeldItemStack(null);
			} else {
				inventory.setHeldItemStack(heldItemStack.copy(overflow));
			}
		} else {
			inventory.setSlot(slotIndex, heldItemStack);
			inventory.setHeldItemStack(currentContent);
		}
	}
}
