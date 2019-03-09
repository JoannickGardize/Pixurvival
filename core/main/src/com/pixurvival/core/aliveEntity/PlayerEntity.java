package com.pixurvival.core.aliveEntity;

import java.nio.ByteBuffer;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.aliveEntity.ability.AbilitySet;
import com.pixurvival.core.aliveEntity.ability.Activity;
import com.pixurvival.core.aliveEntity.ability.CraftingActivity;
import com.pixurvival.core.aliveEntity.ability.HarvestingActivity;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.message.PlayerData;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends AliveEntity<PlayerEntity> implements InventoryHolder, EquipmentHolder {

	private @Setter Activity activity = Activity.NONE;

	private @Setter String name;

	private PlayerInventory inventory;

	private Equipment equipment = new Equipment();

	private StatSet stats = new StatSet();

	@Setter
	private Position chunkPosition;

	public PlayerEntity() {
		equipment.addListener(stats);
	}

	public void setInventory(PlayerInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isServer()) {
			setInventory(new PlayerInventory(getInventorySize()));
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
		return stats.getValue(StatType.MAX_HEALTH);
	}

	@Override
	public double getSpeedPotential() {
		return stats.getValue(StatType.SPEED) * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor();
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

	public PlayerData getData() {
		PlayerData data = new PlayerData();
		data.setId(getId());
		data.setName(name);
		data.setStrength(stats.get(StatType.STRENGTH).getBase());
		data.setAgility(stats.get(StatType.AGILITY).getBase());
		data.setIntelligence(stats.get(StatType.INTELLIGENCE).getBase());
		data.setEquipment(equipment);
		return data;
	}

	public void applyData(PlayerData data) {
		name = data.getName();
		stats.get(StatType.STRENGTH).setBase(data.getStrength());
		stats.get(StatType.AGILITY).setBase(data.getAgility());
		stats.get(StatType.INTELLIGENCE).setBase(data.getIntelligence());
		equipment.set(data.getEquipment());
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
			buffer.putShort((short) craftingActivity.getItemCraft().getId());
			buffer.putDouble(craftingActivity.getProgress());
			break;
		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setMovingAngle(buffer.getDouble());
		setForward(buffer.get() == 1);
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
					HarvestingActivity harvestingActivity = new HarvestingActivity(this, (HarvestableStructure) tile.getStructure());
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
				CraftingActivity activity = new CraftingActivity(this, getWorld().getContentPack().getItemCrafts().get(craftId));
				setActivity(activity);
				activity.setProgressTime(progressTime);
			} else {
				((CraftingActivity) getActivity()).setProgressTime(progressTime);
			}
			break;
		}
	}

	@Override
	public AbilitySet<PlayerEntity> getAbilitySet() {
		// TODO Auto-generated method stub
		return null;
	}
}
