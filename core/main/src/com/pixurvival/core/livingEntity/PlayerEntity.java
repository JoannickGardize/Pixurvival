package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.role.Role;
import com.pixurvival.core.contentPack.gameMode.role.TeamSurvivedWinCondition;
import com.pixurvival.core.contentPack.gameMode.role.WinCondition;
import com.pixurvival.core.contentPack.item.AccessoryItem;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.EquipableItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.item.WeaponItem;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CooldownAbilityData;
import com.pixurvival.core.livingEntity.ability.CraftAbility;
import com.pixurvival.core.livingEntity.ability.CraftAbilityData;
import com.pixurvival.core.livingEntity.ability.DeconstructAbility;
import com.pixurvival.core.livingEntity.ability.DeconstructAbilityData;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityProxy;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
import com.pixurvival.core.livingEntity.ability.HarvestAbility;
import com.pixurvival.core.livingEntity.ability.HarvestAbilityData;
import com.pixurvival.core.livingEntity.ability.SilenceAbility;
import com.pixurvival.core.livingEntity.ability.UseItemAbility;
import com.pixurvival.core.livingEntity.ability.UseItemAbilityData;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends LivingEntity implements EquipmentHolder, CommandExecutor, ChatSender {

	public static final float COLLISION_RADIUS = 0.42f;
	public static final float MAX_HUNGER = 100;
	public static final float HUNGER_DECREASE = 100f / (60 * 10);
	public static final float HUNGER_DAMAGE = 10;

	public static final int INVENTORY_SIZE = 32;

	public static final int CRAFT_ABILITY_ID = 1;
	public static final int HARVEST_ABILITY_ID = 2;
	public static final int USE_ITEM_ABILITY_ID = 3;
	public static final int DECONSTRUCT_ABILITY_ID = 4;

	public static final byte UPDATE_CONTENT_MASK_EQUIPMENT = LivingEntity.NEXT_UPDATE_CONTENT_MASK;

	private static final AbilitySet PLAYER_ABILITY_SET = new AbilitySet();

	private static final Kryo INVENTORY_KRYO = new Kryo();

	static {
		PLAYER_ABILITY_SET.add(new SilenceAbility());
		PLAYER_ABILITY_SET.add(new CraftAbility());
		PLAYER_ABILITY_SET.add(new HarvestAbility());
		PLAYER_ABILITY_SET.add(new UseItemAbility());
		PLAYER_ABILITY_SET.add(new DeconstructAbility());
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_BASE));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.WEAPON_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY1_SPECIAL));
		PLAYER_ABILITY_SET.add(new EquipmentAbilityProxy(EquipmentAbilityType.ACCESSORY2_SPECIAL));

		INVENTORY_KRYO.setReferences(false);
		INVENTORY_KRYO.register(ItemStack.class, new ItemStack.Serializer());
		INVENTORY_KRYO.register(PlayerInventory.class, new PlayerInventory.Serializer());
	}

	private @Setter boolean operator = false;

	private @Setter String name = "Unknown";

	private @Setter PlayerInventory inventory;

	private Equipment equipment = new Equipment();

	private float hunger = MAX_HUNGER;

	private ChunkGroupChangeHelper chunkVision = new ChunkGroupChangeHelper();

	private @Setter PlayerMovementRequest lastPlayerMovementRequest = new PlayerMovementRequest();

	private @Setter int nextAccessorySwitch = Equipment.ACCESSORY1_INDEX;

	private List<SoundEffect> soundEffectsToConsume = new ArrayList<>();

	private @Setter Role role;

	private @Getter ItemCraftDiscovery itemCraftDiscovery;

	private @Getter @Setter long respawnTime;

	@Override
	public void update() {
		addHungerSneaky(-(HUNGER_DECREASE * getWorld().getTime().getDeltaTime()));
		if (hunger <= 0) {
			takeTrueDamageSneaky(HUNGER_DAMAGE * getWorld().getTime().getDeltaTime());
		}
		if (getChunk() == null) {
			foreachChunkInView(chunk -> getWorld().getMap().notifyEnterView(this, chunk.getPosition()));
		}
		super.update();
		chunkVision.move(getPosition(), GameConstants.PLAYER_VIEW_DISTANCE, position -> getWorld().getMap().notifyEnterView(this, position),
				position -> getWorld().getMap().notifyExitView(this, position));
	}

	@Override
	protected void onDeath() {
		if (getWorld().isServer()) {
			getTeam().addDead(this);
			GameMode gameMode = getWorld().getGameMode();
			gameMode.getPlayerDeathItemHandling().getHandler().accept(this);
			respawnTime = gameMode.getPlayerRespawnType().getHandler().applyAsLong(this);
			getWorld().getEntityPool().notifyPlayerDied(this);
			getWorld().getChatManager().received(new ChatEntry(getWorld(), name + " died."));
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isServer()) {
			setInventory(new PlayerInventory(INVENTORY_SIZE));
			equipment.addListener((concernedEquipment, equipmentIndex, previousItemStack, newItemStack) -> {
				setStateChanged(true);
				addUpdateContentMask(UPDATE_CONTENT_MASK_EQUIPMENT);
				if (previousItemStack != null && getWorld().isServer()) {
					((EquipableItem) previousItemStack.getItem()).getStatModifiers().forEach(m -> getStats().removeModifier(m));
				}
				if (newItemStack != null) {
					((EquipableItem) newItemStack.getItem()).getStatModifiers().forEach(m -> getStats().addModifier(m));
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
			itemCraftDiscovery = new ItemCraftDiscovery(inventory, getWorld().getContentPack().getItemCrafts());
		}
	}

	@Override
	public void setTeam(Team team) {
		if (getTeam() == team) {
			return;
		}
		if (getTeam() != null) {
			getTeam().remove(this);
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
	public float getCollisionRadius() {
		return COLLISION_RADIUS;
	}

	public void addHunger(float hungerToAdd) {
		addHungerSneaky(hungerToAdd);
		setStateChanged(true);
		addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
	}

	public void addHungerSneaky(float hungerToAdd) {
		hunger = MathUtils.clamp(hunger + hungerToAdd, 0, MAX_HUNGER);
	}

	public void craft(ItemCraft itemCraft) {
		if (itemCraftDiscovery == null || itemCraftDiscovery.isDiscovered(itemCraft)) {
			((CraftAbilityData) getAbilityData(CRAFT_ABILITY_ID)).setItemCraft(itemCraft);
			startAbility(CRAFT_ABILITY_ID);
		}
	}

	public void harvest(HarvestableMapStructure harvestableStructure) {
		((HarvestAbilityData) getAbilityData(HARVEST_ABILITY_ID)).setStructure(harvestableStructure);
		startAbility(HARVEST_ABILITY_ID);
	}

	public void deconstruct(MapStructure structure) {
		((DeconstructAbilityData) getAbilityData(DECONSTRUCT_ABILITY_ID)).setStructure(structure);
		startAbility(DECONSTRUCT_ABILITY_ID);
	}

	public void useItem(EdibleItem edibleItem, int slotIndex) {
		((UseItemAbilityData) getAbilityData(USE_ITEM_ABILITY_ID)).setEdibleItem(edibleItem);
		((UseItemAbilityData) getAbilityData(USE_ITEM_ABILITY_ID)).setSlotIndex(slotIndex);
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
		if (getWorld().isServer()) {
			lastPlayerMovementRequest.apply(this);
		}
	}

	@Override
	protected void collisionLockEnded() {
		if (getWorld().isServer()) {
			lastPlayerMovementRequest.apply(this);
		}
	}

	@Override
	protected void writeUpdate(ByteBuffer buffer, byte updateFlagsToSend) {
		super.writeUpdate(buffer, updateFlagsToSend);
		if ((updateFlagsToSend & UPDATE_CONTENT_MASK_EQUIPMENT) != 0) {
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getClothing());
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getWeapon());
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getAccessory1());
			ByteBufferUtils.writeItemOrNull(buffer, equipment.getAccessory2());
		}
	}

	@Override
	protected void applyUpdate(ByteBuffer buffer, byte updateContentFlag) {
		super.applyUpdate(buffer, updateContentFlag);
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
	public void writeRepositoryUpdate(ByteBuffer byteBuffer) {
		super.writeRepositoryUpdate(byteBuffer);
		ByteBufferUtils.putBooleans(byteBuffer, isAlive(), isOperator());
		if (!isAlive()) {
			VarLenNumberIO.writePositiveVarLong(byteBuffer, respawnTime);
		}
		ByteBufferUtils.putString(byteBuffer, name);
		try (Output output = new Output(byteBuffer.array())) {
			output.setPosition(byteBuffer.position());
			INVENTORY_KRYO.writeObject(output, inventory);
			byteBuffer.position(output.position());
		}
		itemCraftDiscovery.write(byteBuffer);
	}

	@Override
	public void applyRepositoryUpdate(ByteBuffer byteBuffer) {
		super.applyRepositoryUpdate(byteBuffer);
		byte boolMask = ByteBufferUtils.getBooleansMask(byteBuffer);
		setAlive(ByteBufferUtils.getBoolean1(boolMask));
		setOperator(ByteBufferUtils.getBoolean2(boolMask));
		if (!isAlive()) {
			respawnTime = VarLenNumberIO.readPositiveVarLong(byteBuffer);
		}
		setName(ByteBufferUtils.getString(byteBuffer));
		try (Input input = new Input(byteBuffer.array())) {
			input.setPosition(byteBuffer.position());
			inventory.set(INVENTORY_KRYO.readObject(input, PlayerInventory.class));
			byteBuffer.position(input.position());
		}
		itemCraftDiscovery.apply(byteBuffer, getWorld().getContentPack().getItems());
		addHealthAdapterListener();
	}

	@Override
	public byte getFullUpdateContentMask() {
		return (byte) (super.getFullUpdateContentMask() | UPDATE_CONTENT_MASK_EQUIPMENT);
	}

	public WinCondition getWinCondition() {
		if (role == null) {
			return new TeamSurvivedWinCondition();
		} else {
			return role.getWinCondition();
		}
	}

	public void addItemCraftDiscoveryListener(ItemCraftDiscoveryListener listener) {
		itemCraftDiscovery.addListener(listener);
	}

	public void respawn(Vector2 respawnPosition) {
		setAlive(true);
		getTeam().addAlive(this);
		setHealth(getMaxHealth());
		addHunger(PlayerEntity.MAX_HUNGER);
		teleport(respawnPosition);
		setChunk(null);
		setForward(false);
		startAbility(-1);
		getWorld().getEntityPool().addOld(this);
		getWorld().getEntityPool().notifyPlayerRespawned(this);
	}
}
