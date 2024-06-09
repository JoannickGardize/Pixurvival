package com.pixurvival.core.livingEntity;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatSender;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.role.Role;
import com.pixurvival.core.contentPack.gameMode.role.TeamSurvivedWinCondition;
import com.pixurvival.core.contentPack.gameMode.role.WinCondition;
import com.pixurvival.core.contentPack.item.*;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.interactionDialog.InteractionDialog;
import com.pixurvival.core.livingEntity.ability.*;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.livingEntity.tag.TagInstance;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.system.interest.EquipmentInterest;
import com.pixurvival.core.system.interest.InterestSubscription;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.*;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class PlayerEntity extends LivingEntity implements EquipmentHolder, CommandExecutor, ChatSender {

    public static final float COLLISION_RADIUS = 0.42f;
    public static final float MAX_HUNGER = 100;

    public static final int INVENTORY_SIZE = 32;

    public static final int CRAFT_ABILITY_ID = 1;
    public static final int HARVEST_ABILITY_ID = 2;
    public static final int USE_ITEM_ABILITY_ID = 3;
    public static final int DECONSTRUCT_ABILITY_ID = 4;

    public static final byte UPDATE_CONTENT_MASK_EQUIPMENT = LivingEntity.NEXT_UPDATE_CONTENT_MASK;

    private static final AbilitySet PLAYER_ABILITY_SET = new AbilitySet();

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
    }

    private @Setter boolean operator = false;

    private @Setter String name = "Unknown";

    private @Setter PlayerInventory inventory;

    private Equipment equipment = new Equipment();

    private int[] equipmentModCounts;

    private float hunger = MAX_HUNGER;

    private ChunkGroupChangeHelper chunkVision = new ChunkGroupChangeHelper();

    private @Setter PlayerMovementRequest lastPlayerMovementRequest = new PlayerMovementRequest();

    private @Setter int nextAccessorySwitch = Equipment.ACCESSORY1_INDEX;

    private List<SoundEffect> soundEffectsToConsume = new ArrayList<>();

    private @Setter Role role;

    private ItemCraftDiscovery itemCraftDiscovery;

    private @Setter long respawnTime;

    private long spawnProtectionEndTime;

    private @Setter InteractionDialog interactionDialog;

    @Override
    public void update() {
        if (getChunk() == null) {
            foreachChunkInView(chunk -> getWorld().getMap().notifyEnterView(this, chunk.getPosition()));
        }
        super.update();
        chunkVision.move(getPosition(), GameConstants.PLAYER_VIEW_DISTANCE, position -> getWorld().getMap().notifyEnterView(this, position),
                position -> getWorld().getMap().notifyExitView(this, position));
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        if (getWorld().isServer()) {
            getTeam().addDead(this);
            GameMode gameMode = getWorld().getGameMode();
            gameMode.getPlayerDeathItemHandling().getHandler().accept(this);
            respawnTime = gameMode.getPlayerRespawnType().getHandler().applyAsLong(this);
            if (!gameMode.isKeepPermanentStats()) {
                for (StatType statType : StatType.values()) {
                    getStats().get(statType).setBase(0);
                }
            }
            getWorld().getEntityPool().notifyPlayerDied(this);
            getWorld().getChatManager().received(new ChatEntry(getWorld(), name + " died."));
        }
    }

    @Override
    public void initialize() {
        super.initialize();

        if (getWorld().isServer()) {
            setInventory(new PlayerInventory(INVENTORY_SIZE));
            equipmentModCounts = new int[Equipment.EQUIPMENT_SIZE];
            if (getWorld().isServer()) {
                equipment.addListener((concernedEquipment, equipmentIndex, previousItemStack, newItemStack) -> {
                    equipmentModCounts[equipmentIndex]++;
                    if (previousItemStack != null) {
                        ((EquipableItem) previousItemStack.getItem()).getStatModifiers().forEach(m -> getStats().removeModifier(m));
                    }
                });
            }
            equipment.addListener((concernedEquipment, equipmentIndex, previousItemStack, newItemStack) -> {
                setStateChanged(true);
                addUpdateContentMask(UPDATE_CONTENT_MASK_EQUIPMENT);
                InterestSubscription<EquipmentInterest> interest = getWorld().getInterestSubscriptionSet().get(EquipmentInterest.class);

                if (previousItemStack != null) {
                    if (getWorld().isServer()) {
                        ((EquipableItem) previousItemStack.getItem()).getStatModifiers().forEach(m -> getStats().removeModifier(m));
                    }
                    interest.publish(ei -> ei.unequipped(this, equipmentIndex, (EquipableItem) previousItemStack.getItem()));
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
                    interest.publish(ei -> ei.equipped(this, equipmentIndex, (EquipableItem) newItemStack.getItem()));
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

    public void harvest(HarvestableStructureEntity harvestableStructure) {
        ((HarvestAbilityData) getAbilityData(HARVEST_ABILITY_ID)).setStructure(harvestableStructure);
        startAbility(HARVEST_ABILITY_ID);
    }

    public void deconstruct(StructureEntity structure) {
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

    @Override
    protected IndexMap<TagInstance> getInitialTagMap() {
        return IndexMap.create(getWorld().getContentPack().getTags().size() - 1);
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
    public void writeRepositoryUpdate(ByteBuffer buffer) {
        super.writeRepositoryUpdate(buffer);
        ByteBufferUtils.putBooleans(buffer, isAlive(), isOperator());
        if (!isAlive()) {
            VarLenNumberIO.writePositiveVarLong(buffer, respawnTime);
        }
        VarLenNumberIO.writePositiveVarLong(buffer, spawnProtectionEndTime);
        ByteBufferUtils.putString(buffer, name);
        inventory.write(buffer);
        itemCraftDiscovery.write(buffer);
        ByteBufferUtils.putInts(buffer, equipmentModCounts);
    }

    @Override
    public void applyRepositoryUpdate(ByteBuffer buffer) {
        super.applyRepositoryUpdate(buffer);
        byte boolMask = ByteBufferUtils.getBooleansMask(buffer);
        setAlive(ByteBufferUtils.getBoolean1(boolMask));
        setOperator(ByteBufferUtils.getBoolean2(boolMask));
        if (!isAlive()) {
            respawnTime = VarLenNumberIO.readPositiveVarLong(buffer);
        }
        setSpawnProtectionEndTime(VarLenNumberIO.readPositiveVarLong(buffer));
        setName(ByteBufferUtils.getString(buffer));
        inventory.apply(getWorld(), buffer);
        itemCraftDiscovery.apply(buffer, getWorld().getContentPack().getItems());
        equipmentModCounts = ByteBufferUtils.getInts(buffer);
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
        setSpawnProtectionEndTime();
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

    public void setSpawnProtectionEndTime() {
        setSpawnProtectionEndTime(getWorld().getTime().getTimeMillis()
                + getWorld().getGameMode().getSpawnProtectionDuration());
    }

    public void setSpawnProtectionEndTime(long spawnProtectionEndTime) {
        this.spawnProtectionEndTime = spawnProtectionEndTime;
        setInvincibleTermTime(spawnProtectionEndTime);
    }

    @Override
    public boolean isHiddenForEnemies() {
        return getWorld().getTime().getTimeMillis() < spawnProtectionEndTime;
    }
}
