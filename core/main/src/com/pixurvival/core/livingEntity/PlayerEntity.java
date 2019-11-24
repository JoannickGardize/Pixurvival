package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CooldownAbilityData;
import com.pixurvival.core.livingEntity.ability.CraftAbility;
import com.pixurvival.core.livingEntity.ability.CraftAbilityData;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityProxy;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.HarvestAbilityData;
import com.pixurvival.core.livingEntity.ability.SilenceAbility;
import com.pixurvival.core.livingEntity.ability.SilenceAbilityData;
import com.pixurvival.core.livingEntity.ability.UseItemAbility;
import com.pixurvival.core.livingEntity.ability.UseItemAbilityData;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.MathUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends LivingEntity implements InventoryHolder, EquipmentHolder, CommandExecutor, ChatSender {

	public static final float MAX_HUNGER = 100;
	public static final float HUNGER_DECREASE = 100f / (60 * 10);
	public static final float HUNGER_DAMAGE = 10;

	public static final int INVENTORY_SIZE = 32;

	public static final int SILENCE_ABILITY_ID = 0;
	public static final int CRAFT_ABILITY_ID = 1;
	public static final int HARVEST_ABILITY_ID = 2;
	public static final int USE_ITEM_ABILITY_ID = 3;

	public static final byte UPDATE_CONTENT_MASK_EQUIPMENT = LivingEntity.NEXT_UPDATE_CONTENT_MASK;

	private static final AbilitySet PLAYER_ABILITY_SET = new AbilitySet();

	static {
		PLAYER_ABILITY_SET.add(new SilenceAbility());
		PLAYER_ABILITY_SET.add(new CraftAbility());
		PLAYER_ABILITY_SET.add(new HarvestAbility());
		PLAYER_ABILITY_SET.add(new UseItemAbility());
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_BASE));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY1_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY2_SPECIAL));
	}

	private @Getter @Setter boolean operator = false;

	private @Setter String name = "Unknown";

	private PlayerInventory inventory;

	private Equipment equipment = new Equipment();

	private @Getter float hunger = MAX_HUNGER;

	private ChunkGroupChangeHelper chunkVision = new ChunkGroupChangeHelper();

	private @Setter PlayerMovementRequest lastPlayerMovementRequest = new PlayerMovementRequest();

	private @Setter int nextAccessorySwitch = Equipment.ACCESSORY1_INDEX;

	public PlayerEntity() {
		equipment.addListener((concernedEquipment, equipmentIndex, previousItemStack, newItemStack) -> {
			setStateChanged(true);
			addUpdateContentMask(UPDATE_CONTENT_MASK_EQUIPMENT);
			if (previousItemStack != null && getWorld().isServer()) {
				((EquipableItem) previousItemStack.getItem()).getStatModifiers().forEach(m -> getStats().removeModifier(m));
			}
			if (newItemStack != null) {
				if (getWorld().isServer()) {
					((EquipableItem) newItemStack.getItem()).getStatModifiers().forEach(m -> getStats().addModifier(m));
				}
				if (equipmentIndex == Equipment.WEAPON_INDEX) {
					WeaponItem weaponItem = (WeaponItem) newItemStack.getItem();
					CooldownAbilityData abilityData = (CooldownAbilityData) getAbilityData(EquipmentAbilityType.WEAPON_BASE.getAbilityId());
					abilityData.setReadyTimeMillis(getWorld().getTime().getTimeMillis() + weaponItem.getBaseAbility().getCooldown());
					abilityData = (CooldownAbilityData) getAbilityData(EquipmentAbilityType.WEAPON_SPECIAL.getAbilityId());
					abilityData.setReadyTimeMillis(getWorld().getTime().getTimeMillis() + weaponItem.getSpecialAbility().getCooldown());
				} else if (equipmentIndex == Equipment.ACCESSORY1_INDEX) {
					AccessoryItem accessoryItem = (AccessoryItem) newItemStack.getItem();
					CooldownAbilityData abilityData = (CooldownAbilityData) getAbilityData(EquipmentAbilityType.ACCESSORY1_SPECIAL.getAbilityId());
					abilityData.setReadyTimeMillis(getWorld().getTime().getTimeMillis() + accessoryItem.getAbility().getCooldown());
				} else if (equipmentIndex == Equipment.ACCESSORY2_INDEX) {
					AccessoryItem accessoryItem = (AccessoryItem) newItemStack.getItem();
					CooldownAbilityData abilityData = (CooldownAbilityData) getAbilityData(EquipmentAbilityType.ACCESSORY2_SPECIAL.getAbilityId());
					abilityData.setReadyTimeMillis(getWorld().getTime().getTimeMillis() + accessoryItem.getAbility().getCooldown());
				}
			}
		});
	}

	public void setInventory(PlayerInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void update() {
		addHunger(-(float) (HUNGER_DECREASE * getWorld().getTime().getDeltaTime()));
		if (hunger <= 0) {
			takeTrueDamageSneaky(HUNGER_DAMAGE * (float) getWorld().getTime().getDeltaTime());
		}
		super.update();
		chunkVision.move(getPosition(), GameConstants.PLAYER_VIEW_DISTANCE, position -> getWorld().getMap().notifyEnterView(this, position),
				position -> getWorld().getMap().notifyExitView(this, position));
	}

	@Override
	protected void onDeath() {
		if (getWorld().isServer()) {
			getTeam().addDead(this);
		}
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
		setStateChanged(true);
		addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
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
		startAbility(type.getAbilityId());
	}

	@Override
	public AbilitySet getAbilitySet() {
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

	@Override
	protected void fixedMovementEnded() {
		lastPlayerMovementRequest.apply(this);
	}

	public void silence(long duration) {
		startAbility(SILENCE_ABILITY_ID);
		((SilenceAbilityData) getAbilityData(SILENCE_ABILITY_ID)).setEndTime(getWorld().getTime().getTimeMillis() + duration);
	}

	@Override
	protected void collisionLockEnded() {
		lastPlayerMovementRequest.apply(this);
	}

	@Override
	protected void writeUpdate(ByteBuffer buffer, byte updateFlagsToSend) {
		if ((updateFlagsToSend & UPDATE_CONTENT_MASK_EQUIPMENT) != 0) {
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getClothing());
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getWeapon());
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getAccessory1());
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getAccessory2());
		}
	}

	@Override
	protected void applyUpdate(ByteBuffer buffer, byte updateContentFlag) {
		if ((updateContentFlag & UPDATE_CONTENT_MASK_EQUIPMENT) != 0) {
			List<Item> itemList = getWorld().getContentPack().getItems();
			equipment.setClothing(ByteBufferUtils.readItemOrNullAsItemStack(buffer, itemList));
			equipment.setWeapon(ByteBufferUtils.readItemOrNullAsItemStack(buffer, itemList));
			equipment.setAccessory1(ByteBufferUtils.readItemOrNullAsItemStack(buffer, itemList));
			equipment.setAccessory2(ByteBufferUtils.readItemOrNullAsItemStack(buffer, itemList));
		}
	}

	@Override
	protected void writeAdditionnalOtherPart(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(hunger);
	}

	@Override
	protected void applyAdditionnalOtherPart(ByteBuffer byteBuffer) {
		hunger = byteBuffer.getFloat();
	}

	@Override
	public byte getFullUpdateContentMask() {
		return (byte) (super.getFullUpdateContentMask() | UPDATE_CONTENT_MASK_EQUIPMENT);
	}
}
