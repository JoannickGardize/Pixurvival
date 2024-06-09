package com.pixurvival.core.livingEntity;

import com.pixurvival.core.Healable;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.alteration.PersistentAlteration;
import com.pixurvival.core.alteration.PersistentAlterationEntry;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;
import com.pixurvival.core.contentPack.tag.Tag;
import com.pixurvival.core.contentPack.tag.TagValue;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilityData;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.SilenceAbilityData;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.livingEntity.stats.StatType;
import com.pixurvival.core.livingEntity.tag.RemoveTagAction;
import com.pixurvival.core.livingEntity.tag.TagInstance;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.IndexMap;
import com.pixurvival.core.util.VarLenNumberIO;
import com.pixurvival.core.util.Vector2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class LivingEntity extends Entity implements Healable, TeamMember, InventoryHolder {

    public static final int SILENCE_ABILITY_ID = 0;

    public static final byte UPDATE_CONTENT_MASK_STATS = 1;
    public static final byte UPDATE_CONTENT_MASK_ABILITY = 2;
    public static final byte UPDATE_CONTENT_MASK_OTHERS = 4;
    public static final byte UPDATE_CONTENT_MASK_FORWARD_BOOL = 8;
    public static final byte UPDATE_CONTENT_MASK_MOVEMENT_CHANGE_ENABLED_BOOL = 16;
    public static final byte UPDATE_CONTENT_MASK_TAG = 32;
    public static final byte NEXT_UPDATE_CONTENT_MASK = 64;

    private byte updateContentFlags = getFullUpdateContentMask();

    private float health;

    private StatSet stats = new StatSet();

    private AbilityData[] abilityData;
    private Ability currentAbility;

    private Vector2 targetPosition = new Vector2();

    private List<PersistentAlterationEntry> persistentAlterationEntries = new ArrayList<>();
    private @Getter(AccessLevel.NONE) List<PersistentAlterationEntry> newPersistentAlterationEntries = new ArrayList<>();

    private @Setter Team team = TeamSet.WILD_TEAM;

    private @Setter boolean movementChangeEnabled = true;
    private SpriteSheet overridingSpriteSheet = null;

    private long stunTermTime = 0;
    private long invincibleTermTime = 0;

    @Setter
    private Vector2 spawnPosition;

    @Setter
    private IndexMap<TagInstance> tags;

    private int tagInstanceModCount = 0;

    @Override
    protected boolean canForward() {
        return getWorld().getTime().getTimeMillis() >= stunTermTime;
    }

    @Override
    public void setStateChanged(boolean stateChanged) {
        if (!stateChanged) {
            updateContentFlags = 0;
        }
        super.setStateChanged(stateChanged);
    }

    public void addUpdateContentMask(byte mask) {
        updateContentFlags |= mask;
    }

    public void stun(long duration) {
        long term = duration + getWorld().getTime().getTimeMillis();
        if (term > stunTermTime) {
            stunTermTime = term;
            setStateChanged(true);
            addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
        }
    }

    public void setInvincible(long duration) {
        long term = duration + getWorld().getTime().getTimeMillis();
        if (term > invincibleTermTime) {
            invincibleTermTime = term;
        }
    }

    public void setInvincibleTermTime(long invincibleTermTime) {
        if (invincibleTermTime > this.invincibleTermTime) {
            this.invincibleTermTime = invincibleTermTime;
        }
    }

    public void setFixedMovement(float movingAngle, float speed) {
        movementChangeEnabled = true;
        setForwardFactor(1);
        setMovingAngle(movingAngle);
        setSpeed(speed * getForwardFactor() * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor());
        setForward(true);
        updateVelocity();
        movementChangeEnabled = false;
    }

    @Override
    public boolean setSpeed(float speed) {
        if (movementChangeEnabled && super.setSpeed(speed)) {
            setStateChanged(true);
            return true;
        }
        return false;
    }

    public void setOverridingSpriteSheet(SpriteSheet spriteSheet) {
        if (overridingSpriteSheet != spriteSheet) {
            overridingSpriteSheet = spriteSheet;
            setStateChanged(true);
            addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
        }
    }

    public void stopFixedMovement() {
        movementChangeEnabled = true;
        fixedMovementEnded();
    }

    protected void fixedMovementEnded() {
    }

    /**
     * Called at entity creation or at reload.
     */
    @Override
    public void initialize() {
        super.initialize();
        health = getMaxHealth();
        if (getWorld().isServer()) {
            stats.addListener((o, s) -> {
                setStateChanged(true);
                addUpdateContentMask(UPDATE_CONTENT_MASK_STATS);
            });
        }
        initializeAbilityData();
    }

    /**
     * Called at entity creation only.
     */
    @Override
    public void initializeAtCreation() {
        addHealthAdapterListener();
        if (getWorld().isServer()) {
            spawnPosition = getPosition().copy();
        }
        tags = getInitialTagMap();
    }

    protected void addHealthAdapterListener() {
        stats.get(StatType.MAX_HEALTH).addListener((o, s) -> {
            float percentHealth = getHealth() / o;
            setHealth(s.getValue() * percentHealth);
        });
    }

    private void initializeAbilityData() {
        AbilitySet abilitySet = getAbilitySet();
        abilityData = new AbilityData[abilitySet.size()];
        for (int i = 0; i < abilitySet.size(); i++) {
            abilityData[i] = abilitySet.get(i).createAbilityData();
        }
    }

    /**
     * Change the position of the entity. Must be called instead of directly
     * changing the position to mark the state of this entity to changed.
     *
     * @param position
     */
    public void teleport(Vector2 position) {
        getPosition().set(position);
        setStateChanged(true);
    }

    public void setHealth(float health) {
        if (health != this.health) {
            this.health = health;
            setStateChanged(true);
        }
    }

    @Override
    public void setForward(boolean forward) {
        if (movementChangeEnabled && forward != isForward()) {
            super.setForward(forward);
            setStateChanged(true);
        }
    }

    @Override
    public void setMovingAngle(float movingAngle) {
        if (movementChangeEnabled && movingAngle != getMovingAngle()) {
            super.setMovingAngle(movingAngle);
            setStateChanged(true);
        }
    }

    @Override
    public void setForwardFactor(float forwardFactor) {
        if (forwardFactor != getForwardFactor()) {
            super.setForwardFactor(forwardFactor);
            setStateChanged(true);
            addUpdateContentMask(UPDATE_CONTENT_MASK_OTHERS);
        }
    }

    @Override
    public float getMaxHealth() {
        return stats.getValue(StatType.MAX_HEALTH);
    }

    @Override
    public float getSpeedPotential() {
        if (movementChangeEnabled) {
            if (isSolid()) {
                return stats.getValue(StatType.SPEED) * getWorld().getMap().tileAt(getPosition()).getTileDefinition().getVelocityFactor();
            } else {
                return stats.getValue(StatType.SPEED);
            }
        } else {
            return getSpeed();
        }
    }

    @Override
    public void takeDamage(float amount, DamageAttributes attributes) {
        takeTrueDamage(attributes.isTrueDamage() ? amount : amount * (1 - stats.getValue(StatType.ARMOR)), attributes);
    }

    public void takeTrueDamage(float amount, DamageAttributes attributes) {
        takeTrueDamageSneaky(amount, attributes);
        setStateChanged(true);
    }

    public void takeTrueDamageSneaky(float amount, DamageAttributes attributes) {
        if (!attributes.isBypassInvincibility() && getWorld().getTime().getTimeMillis() < invincibleTermTime) {
            return;
        }
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    @Override
    public void takeHeal(float amount) {
        health += amount;
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        setStateChanged(true);
    }

    public void applyPersistentAlteration(TeamMember source, PersistentAlteration alteration) {
        newPersistentAlterationEntries.add(new PersistentAlterationEntry(source, alteration));

    }

    private void insertNewPersistentAlterations() {
        // Classical for mandatory here because the list can grow during the loop
        for (int i = 0; i < newPersistentAlterationEntries.size(); i++) {
            PersistentAlterationEntry entry = newPersistentAlterationEntries.get(i);
            switch (entry.getAlteration().getStackPolicy()) {
                case IGNORE:
                    if (!persistentAlterationEntries.contains(entry)) {
                        persistentAlterationEntries.add(entry);
                        beginPersistentAlteration(entry);
                    }
                    break;
                case REPLACE:
                    int index = persistentAlterationEntries.indexOf(entry);
                    if (index == -1) {
                        persistentAlterationEntries.add(entry);
                    } else {
                        PersistentAlterationEntry oldEntry = persistentAlterationEntries.get(index);
                        oldEntry.getAlteration().end(oldEntry.getSource(), this, oldEntry.getData());
                        persistentAlterationEntries.set(index, entry);
                    }
                    beginPersistentAlteration(entry);
                    break;
                case STACK:
                    persistentAlterationEntries.add(entry);
                    beginPersistentAlteration(entry);
                    break;
                default:
                    break;
            }
        }
        newPersistentAlterationEntries.clear();
    }

    private void beginPersistentAlteration(PersistentAlterationEntry entry) {
        // Begin before fixing the term time, because some type of
        // PersistentAlteration
        // fixes the term during this method
        entry.setData(entry.getAlteration().begin(entry.getSource(), this));
        entry.setTermTimeMillis(entry.getAlteration().getDuration() + getWorld().getTime().getTimeMillis());
    }


    public void applyTag(TagValue tagValue, long duration) {
        Tag tag = tagValue.getTag();
        int id = tag.getId();
        TagInstance tagInstance = tags.get(id);
        if (tagInstance == null) {
            tagInstance = new TagInstance(tagValue);
            tagInstance.setModCount(tagInstanceModCount++);
            tags.put(id, tagInstance);
            if (duration > 0) {
                tagInstance.setExpirationTime(getWorld().getActionTimerManager().addActionTimer(
                        new RemoveTagAction(getId(), id, tagInstanceModCount), duration));
            }
        } else {
            switch (tag.getValueStackPolicy()) {
                case ADD:
                    if (tagValue.getValue() != 0) {
                        tagInstance = tags.captureValueChange(tagInstance);
                        tagInstance.setValue(tagInstance.getValue() + tagValue.getValue());
                        tagInstance.setModCount(tagInstanceModCount++);
                    }
                    break;
                case REPLACE:
                    if (tagValue.getValue() != tagInstance.getValue()) {
                        tagInstance = tags.captureValueChange(tagInstance);
                        tagInstance.setValue(tagInstance.getValue() + tagValue.getValue());
                        tagInstance.setModCount(tagInstanceModCount++);
                    }
                    break;
            }
            switch (tag.getDurationStackPolicy()) {
                case ADD:
                    if (duration != 0) {
                        tagInstance = tags.captureValueChange(tagInstance);
                        tagInstance.setModCount(tagInstanceModCount++);
                        tagInstance.setExpirationTime(getWorld().getActionTimerManager().addActionTimer(
                                new RemoveTagAction(getId(), id, tagInstance.getModCount()),
                                tagInstance.getExpirationTime() - getWorld().getTime().getTimeMillis() + duration));
                    }
                    break;
                case REPLACE:
                    // Unchanged case not checked because it's highly improbable
                    tagInstance = tags.captureValueChange(tagInstance);
                    tagInstance.setModCount(tagInstanceModCount++);
                    tagInstance.setExpirationTime(getWorld().getActionTimerManager().addActionTimer(
                            new RemoveTagAction(getId(), id, tagInstance.getModCount()), duration));
                    break;
            }
        }
    }

    public void removeTag(int tagId) {
        // TODO update for removed values?
    }

    @Override
    public void update() {
        long timeMillis = getWorld().getTime().getTimeMillis();

        persistentAlterationEntries.removeIf(entry -> {
            entry.setData(entry.getAlteration().update(entry.getSource(), this, entry.getData()));
            if (timeMillis >= entry.getTermTimeMillis()) {
                entry.getAlteration().end(entry.getSource(), this, entry.getData());
                return true;
            }
            return false;
        });

        // Only server has the final decision to kill an alive entity
        if (health <= 0 && getWorld().isServer()) {
            setAlive(false);
        }
        if (currentAbility != null) {
            if (!currentAbility.canMove() && isForward()) {
                stopCurrentAbility();
            } else if (currentAbility.update(this)) {
                currentAbility = null;
            }
        }
        insertNewPersistentAlterations();
        super.update();
    }

    public AbilityData getAbilityData(int abilityId) {
        return abilityData[abilityId];
    }

    public AbilityData setAbilityData(int abilityId) {
        return abilityData[abilityId];
    }

    public void startAbility(int abilityId) {
        if (currentAbility != null && (currentAbility.getId() == abilityId || !currentAbility.stop(this))) {
            return;
        }
        if (abilityId < 0 || abilityId >= getAbilitySet().size()) {
            currentAbility = null;
        } else {
            Ability ability = getAbilitySet().get(abilityId);
            if (!ability.start(this)) {
                return;
            }
            currentAbility = ability;
        }
        setStateChanged(true);
        addUpdateContentMask(UPDATE_CONTENT_MASK_ABILITY);
    }

    public void stopCurrentAbility() {
        if (currentAbility != null && currentAbility.stop(this)) {
            currentAbility = null;
            setStateChanged(true);
            addUpdateContentMask(UPDATE_CONTENT_MASK_ABILITY);
        }
    }

    public void silence(long duration) {
        startAbility(SILENCE_ABILITY_ID);
        ((SilenceAbilityData) getAbilityData(SILENCE_ABILITY_ID)).setEndTime(getWorld().getTime().getTimeMillis() + duration);
    }

    @Override
    public final void writeUpdate(ByteBuffer buffer, boolean full) {
        writeUpdate(buffer, full ? getFullUpdateContentMask() : updateContentFlags);
    }

    @Override
    public void applyUpdate(ByteBuffer buffer) {
        applyUpdate(buffer, buffer.get());
    }

    protected void writeUpdate(ByteBuffer buffer, byte updateFlagsToSend) {

        // set flag

        if (isForward()) {
            updateFlagsToSend |= UPDATE_CONTENT_MASK_FORWARD_BOOL;
        }
        if (movementChangeEnabled) {
            updateFlagsToSend |= UPDATE_CONTENT_MASK_MOVEMENT_CHANGE_ENABLED_BOOL;
        }
        buffer.put(updateFlagsToSend);

        // normal part

        buffer.putFloat(getPosition().getX());
        buffer.putFloat(getPosition().getY());
        buffer.putFloat(getMovingAngle());
        if (!movementChangeEnabled) {
            buffer.putFloat(getSpeed());
        }
        buffer.putFloat(getHealth());

        // stats part

        if ((updateFlagsToSend & UPDATE_CONTENT_MASK_STATS) != 0) {
            getStats().writeValues(buffer);
        }

        // ability part

        if ((updateFlagsToSend & UPDATE_CONTENT_MASK_ABILITY) != 0) {
            if (getCurrentAbility() == null) {
                buffer.put(Ability.NONE_ID);
            } else {
                byte id = getCurrentAbility().getId();
                buffer.put(id);
                getAbilityData(id).write(buffer, this);
            }
        }

        // others part

        if ((updateFlagsToSend & UPDATE_CONTENT_MASK_OTHERS) != 0) {
            if (stunTermTime > getWorld().getTime().getTimeMillis()) {
                buffer.put((byte) 1);
                ByteBufferUtils.writeFutureTime(buffer, getWorld(), stunTermTime);
            } else {
                buffer.put((byte) 0);
            }
            VarLenNumberIO.writePositiveVarInt(buffer, getTeam().getId());
            ByteBufferUtils.writeElementOrNull(buffer, overridingSpriteSheet);
            buffer.putFloat(getForwardFactor());
            writeAdditionnalOtherPart(buffer);
        }
    }

    protected void applyUpdate(ByteBuffer buffer, byte updateContentFlag) {

        // normal part

        getPosition().set(buffer.getFloat(), buffer.getFloat());
        if ((updateContentFlag & UPDATE_CONTENT_MASK_MOVEMENT_CHANGE_ENABLED_BOOL) != 0) {
            stopFixedMovement();
            setForward((updateContentFlag & UPDATE_CONTENT_MASK_FORWARD_BOOL) != 0);
            setMovingAngle(buffer.getFloat());
        } else {
            setFixedMovement(buffer.getFloat(), buffer.getFloat());
        }
        setHealth(buffer.getFloat());

        // stats part

        if ((updateContentFlag & UPDATE_CONTENT_MASK_STATS) != 0) {
            getStats().applyValues(buffer);
        }

        // ability part

        if ((updateContentFlag & UPDATE_CONTENT_MASK_ABILITY) != 0) {
            byte abilityId = buffer.get();
            if (abilityId == Ability.NONE_ID) {
                stopCurrentAbility();
            } else {
                getAbilityData(abilityId).apply(buffer, this);
                startAbility(abilityId);
            }
        }

        // tag part

        // TODO : remove old tags

        // others part

        if ((updateContentFlag & UPDATE_CONTENT_MASK_OTHERS) != 0) {
            if (buffer.get() == 1) {
                stunTermTime = ByteBufferUtils.readFutureTime(buffer, getWorld());
            } else {
                stunTermTime = 0;
            }
            setTeam(getWorld().getTeamSet().get(VarLenNumberIO.readPositiveVarInt(buffer)));
            overridingSpriteSheet = ByteBufferUtils.readElementOrNull(buffer, getWorld().getContentPack().getSpriteSheets());
            setForwardFactor(buffer.getFloat());
            applyAdditionnalOtherPart(buffer);
        }
    }

    protected void writeAdditionnalOtherPart(ByteBuffer byteBuffer) {
        // For override
    }

    protected void applyAdditionnalOtherPart(ByteBuffer byteBuffer) {
        // For override
    }

    protected boolean shouldWriteTagMapRepositoryUpdate(ByteBuffer buffer) {
        return true;
    }

    protected boolean shouldReadTagMapRepositoryUpdate(ByteBuffer buffer) {
        return true;
    }

    @Override
    public void writeRepositoryUpdate(ByteBuffer buffer) {
        buffer.putFloat(getStats().get(StatType.STRENGTH).getBase());
        buffer.putFloat(getStats().get(StatType.AGILITY).getBase());
        buffer.putFloat(getStats().get(StatType.INTELLIGENCE).getBase());
        writeUpdate(buffer, (byte) (getFullUpdateContentMask() & ~UPDATE_CONTENT_MASK_STATS));
        buffer.putFloat(spawnPosition.getX());
        buffer.putFloat(spawnPosition.getY());
        VarLenNumberIO.writePositiveVarInt(buffer, persistentAlterationEntries.size());
        for (PersistentAlterationEntry entry : persistentAlterationEntries) {
            TeamMemberSerialization.write(buffer, entry.getSource(), true);
            VarLenNumberIO.writePositiveVarInt(buffer, entry.getAlteration().getId());
            ByteBufferUtils.writeFutureTime(buffer, getWorld(), entry.getTermTimeMillis());
            entry.getAlteration().writeData(buffer, this, entry.getData());
        }

    }

    @Override
    public void applyRepositoryUpdate(ByteBuffer buffer) {
        getStats().get(StatType.STRENGTH).setBase(buffer.getFloat());
        getStats().get(StatType.AGILITY).setBase(buffer.getFloat());
        getStats().get(StatType.INTELLIGENCE).setBase(buffer.getFloat());
        super.applyRepositoryUpdate(buffer);
        spawnPosition = new Vector2(buffer.getFloat(), buffer.getFloat());
        int size = VarLenNumberIO.readPositiveVarInt(buffer);
        for (int i = 0; i < size; i++) {
            TeamMember source = TeamMemberSerialization.read(buffer, getWorld(), true);
            PersistentAlteration alteration = (PersistentAlteration) getWorld().getContentPack().getAlterations().get(VarLenNumberIO.readPositiveVarInt(buffer));
            PersistentAlterationEntry entry = new PersistentAlterationEntry(source, alteration);
            entry.setTermTimeMillis(ByteBufferUtils.readFutureTime(buffer, getWorld()));
            entry.setData(alteration.readData(buffer, this));
            persistentAlterationEntries.add(entry);
            alteration.restore(source, this, entry.getData());
        }
    }

    public abstract AbilitySet getAbilitySet();

    /**
     * The subclasses are responsible for the tag map serialization
     *
     * @return
     */
    protected abstract IndexMap<TagInstance> getInitialTagMap();

    public void prepareTargetedAlteration() {

    }

    /**
     * The update mask containing all flags for full update
     *
     * @return
     */
    public byte getFullUpdateContentMask() {
        return UPDATE_CONTENT_MASK_STATS | UPDATE_CONTENT_MASK_ABILITY | UPDATE_CONTENT_MASK_OTHERS | UPDATE_CONTENT_MASK_TAG;
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        stopCurrentAbility();
    }
}
