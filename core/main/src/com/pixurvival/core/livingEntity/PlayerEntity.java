package com.pixurvival.core.livingEntity;

import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CraftAbility;
import com.pixurvival.core.livingEntity.ability.CraftAbilityData;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.HarvestAbilityData;
import com.pixurvival.core.map.ChunkPosition;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.message.PlayerData;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends LivingEntity implements InventoryHolder, EquipmentHolder {

	public static final int CRAFT_ABILITY_ID = 0;
	public static final int HARVEST_ABILITY_ID = 1;

	private static final AbilitySet PLAYER_ABILITY_SET = new AbilitySet();

	static {
		PLAYER_ABILITY_SET.add(new CraftAbility());
		PLAYER_ABILITY_SET.add(new HarvestAbility());
	}

	private @Setter String name;

	private PlayerInventory inventory;

	private Equipment equipment = new Equipment();

	@Setter
	private ChunkPosition chunkPosition;

	public PlayerEntity() {
		equipment.addListener(getStats());
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

	public void craft(ItemCraft itemCraft) {
		((CraftAbilityData) getAbilityData(CRAFT_ABILITY_ID)).setItemCraft(itemCraft);
		startAbility(CRAFT_ABILITY_ID);
	}

	public void harvest(HarvestableStructure harvestableStructure) {
		((HarvestAbilityData) getAbilityData(HARVEST_ABILITY_ID)).setStructure(harvestableStructure);
		startAbility(HARVEST_ABILITY_ID);
	}

	public PlayerData getData() {
		PlayerData data = new PlayerData();
		data.setId(getId());
		data.setName(name);
		data.setStrength(getStats().get(StatType.STRENGTH).getBase());
		data.setAgility(getStats().get(StatType.AGILITY).getBase());
		data.setIntelligence(getStats().get(StatType.INTELLIGENCE).getBase());
		data.setEquipment(equipment);
		return data;
	}

	public void applyData(PlayerData data) {
		name = data.getName();
		getStats().get(StatType.STRENGTH).setBase(data.getStrength());
		getStats().get(StatType.AGILITY).setBase(data.getAgility());
		getStats().get(StatType.INTELLIGENCE).setBase(data.getIntelligence());
		equipment.set(data.getEquipment());
	}

	@Override
	public AbilitySet getAbilitySet() {
		return PLAYER_ABILITY_SET;
	}
}
