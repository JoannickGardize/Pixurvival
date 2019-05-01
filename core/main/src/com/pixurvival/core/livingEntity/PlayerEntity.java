package com.pixurvival.core.livingEntity;

import java.util.function.Consumer;

import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.Item.Equipable;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CraftAbility;
import com.pixurvival.core.livingEntity.ability.CraftAbilityData;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityProxy;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.HarvestAbilityData;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.map.ChunkPosition;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.message.PlayerData;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends LivingEntity implements InventoryHolder, EquipmentHolder {

	public static final int INVENTORY_SIZE = 32;

	public static final int CRAFT_ABILITY_ID = 0;
	public static final int HARVEST_ABILITY_ID = 1;
	public static final int WEAPON_BASE_ABILITY_ID = 2;
	public static final int WEAPON_SPECIAL_ABILITY_ID = 3;
	public static final int ACCESSORY1_SPECIAL_ABILITY_ID = 4;
	public static final int ACCESSORY2_SPECIAL_ABILITY_ID = 5;

	private static final AbilitySet PLAYER_ABILITY_SET = new AbilitySet();

	static {
		PLAYER_ABILITY_SET.add(new CraftAbility());
		PLAYER_ABILITY_SET.add(new HarvestAbility());
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_BASE));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY1_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY2_SPECIAL));
	}

	private @Setter String name;

	private PlayerInventory inventory;

	private Equipment equipment = new Equipment();

	@Setter
	private ChunkPosition chunkPosition;

	private @Getter short teamId;

	private @Getter @Setter long previousMovementId = -1;

	public PlayerEntity() {
		equipment.addListener((concernedEquipment, equipmentIndex, previousItemStack, newItemStack) -> {
			if (previousItemStack != null) {
				((Equipable) previousItemStack.getItem().getDetails()).getStatModifiers().forEach(m -> getStats().addModifier(m));
			}
			if (newItemStack != null) {
				((Equipable) newItemStack.getItem().getDetails()).getStatModifiers().forEach(m -> getStats().removeModifier(m));
			}
		});
	}

	public void setInventory(PlayerInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isServer()) {
			setInventory(new PlayerInventory(INVENTORY_SIZE));
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
	public double getCollisionRadius() {
		return 0.42;
	}

	public void craft(ItemCraft itemCraft) {
		((CraftAbilityData) getAbilityData(CRAFT_ABILITY_ID)).setItemCraft(itemCraft);
		startAbility(CRAFT_ABILITY_ID);
	}

	public void harvest(HarvestableStructure harvestableStructure) {
		((HarvestAbilityData) getAbilityData(HARVEST_ABILITY_ID)).setStructure(harvestableStructure);
		startAbility(HARVEST_ABILITY_ID);
	}

	public void startEquipmentAbility(EquipmentAbilityType type) {
		if (type == null) {
			stopCurrentAbility();
			return;
		}
		switch (type) {
		case WEAPON_BASE:
			startAbility(WEAPON_BASE_ABILITY_ID);
			break;
		case WEAPON_SPECIAL:
			startAbility(WEAPON_SPECIAL_ABILITY_ID);
			break;
		case ACCESSORY1_SPECIAL:
			startAbility(ACCESSORY1_SPECIAL_ABILITY_ID);
			break;
		case ACCESSORY2_SPECIAL:
			startAbility(ACCESSORY2_SPECIAL_ABILITY_ID);
			break;
		}
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

	@Override
	public void foreach(TargetType targetType, Consumer<LivingEntity> action) {
		switch (targetType) {
		case ALL_ENEMIES:
			foreachEnemies(action);
			break;
		case ALL_ALLIES:
			foreachAllies(action, true);
			break;
		case OTHER_ALLIES:
			foreachAllies(action, false);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void foreachEnemies(Consumer<LivingEntity> action) {
		EntityPool entityPool = getWorld().getEntityPool();
		for (Entity entity : entityPool.get(EntityGroup.PLAYER)) {
			PlayerEntity playerEntity = (PlayerEntity) entity;
			if (playerEntity.teamId != teamId) {
				action.accept(playerEntity);
			}
		}
		entityPool.get(EntityGroup.CREATURE).forEach((Consumer) action);
	}

	public void foreachAllies(Consumer<LivingEntity> action, boolean includeSelf) {
		for (Entity entity : getWorld().getEntityPool().get(EntityGroup.PLAYER)) {
			PlayerEntity playerEntity = (PlayerEntity) entity;
			if (playerEntity.teamId == teamId && (includeSelf || !this.equals(playerEntity))) {
				action.accept(playerEntity);
			}
		}
	}
}
