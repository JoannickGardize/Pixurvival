package com.pixurvival.core.livingEntity;

import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CraftAbility;
import com.pixurvival.core.livingEntity.ability.CraftAbilityData;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityProxy;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.HarvestAbilityData;
import com.pixurvival.core.livingEntity.ability.UseItemAbility;
import com.pixurvival.core.livingEntity.ability.UseItemAbilityData;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.MathUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends LivingEntity implements InventoryHolder, EquipmentHolder, CommandExecutor, ChatSender {

	public static final float MAX_HUNGER = 100;
	public static final float HUNGER_DECREASE = 100f / (60 * 10);
	public static final float HUNGER_DECREASE_MOVE = 100f / (60 * 10);

	public static final int INVENTORY_SIZE = 32;

	public static final int CRAFT_ABILITY_ID = 0;
	public static final int HARVEST_ABILITY_ID = 1;
	public static final int WEAPON_BASE_ABILITY_ID = 2;
	public static final int WEAPON_SPECIAL_ABILITY_ID = 3;
	public static final int ACCESSORY1_SPECIAL_ABILITY_ID = 4;
	public static final int ACCESSORY2_SPECIAL_ABILITY_ID = 5;
	public static final int USE_ITEM_ABILITY_ID = 6;

	private static final AbilitySet<Ability> PLAYER_ABILITY_SET = new AbilitySet<>();

	static {
		PLAYER_ABILITY_SET.add(new CraftAbility());
		PLAYER_ABILITY_SET.add(new HarvestAbility());
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_BASE));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY1_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY2_SPECIAL));
		PLAYER_ABILITY_SET.add(new UseItemAbility());
	}

	// TODO false par défaut et créer le système pour op un joueur coté serveur
	private @Getter @Setter boolean operator = true;

	private @Setter String name = "Unknown";

	private PlayerInventory inventory;

	private Equipment equipment = new Equipment();

	private @Getter float hunger = MAX_HUNGER;

	private @Getter @Setter long previousMovementId = -1;

	private ChunkGroupChangeHelper chunkVision = new ChunkGroupChangeHelper();

	public PlayerEntity() {
		equipment.addListener((concernedEquipment, equipmentIndex, previousItemStack, newItemStack) -> {
			if (previousItemStack != null) {
				((EquipableItem) previousItemStack.getItem()).getStatModifiers().forEach(m -> getStats().removeModifier(m));
			}
			if (newItemStack != null) {
				((EquipableItem) newItemStack.getItem()).getStatModifiers().forEach(m -> getStats().addModifier(m));
			}
		});
	}

	public void setInventory(PlayerInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void update() {
		addHunger(-(float) (HUNGER_DECREASE * getWorld().getTime().getDeltaTime()));
		if (isForward()) {
			addHunger(-(float) (HUNGER_DECREASE_MOVE * getWorld().getTime().getDeltaTime()));
		}
		super.update();
		chunkVision.move(getPosition(), GameConstants.PLAYER_VIEW_DISTANCE, position -> getWorld().getMap().notifyEnterView(this, position),
				position -> getWorld().getMap().notifyExitView(this, position));
	}

	@Override
	protected void onDeath() {
		getTeam().addDead(this);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isServer()) {
			setInventory(new PlayerInventory(INVENTORY_SIZE));
		}
	}

	@Override
	public void setTeam(Team team) {
		if (getTeam() == team) {
			return;
		}
		super.setTeam(team);
		if (isAlive()) {
			team.addAlive(this);
		} else {
			team.addDead(this);
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

	public void addHunger(float hungerToAdd) {
		hunger = MathUtils.clamp(hunger + hungerToAdd, 0, MAX_HUNGER);
	}

	public void craft(ItemCraft itemCraft) {
		((CraftAbilityData) getAbilityData(CRAFT_ABILITY_ID)).setItemCraft(itemCraft);
		startAbility(CRAFT_ABILITY_ID);
	}

	public void harvest(HarvestableMapStructure harvestableStructure) {
		((HarvestAbilityData) getAbilityData(HARVEST_ABILITY_ID)).setStructure(harvestableStructure);
		startAbility(HARVEST_ABILITY_ID);
	}

	public void useItem(EdibleItem edibleItem, int slotIndex) {
		((UseItemAbilityData) getAbilityData(USE_ITEM_ABILITY_ID)).setEdibleItem(edibleItem);
		((UseItemAbilityData) getAbilityData(USE_ITEM_ABILITY_ID)).setIndex(slotIndex);
		startAbility(USE_ITEM_ABILITY_ID);
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
	public AbilitySet<Ability> getAbilitySet() {
		return PLAYER_ABILITY_SET;
	}

	public void foreachChunkInView(Consumer<Chunk> action) {
		getWorld().getMap().forEachChunk(getPosition(), GameConstants.PLAYER_VIEW_DISTANCE, action);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}
}
