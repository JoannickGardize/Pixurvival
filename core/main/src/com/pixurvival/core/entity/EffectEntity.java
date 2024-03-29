package com.pixurvival.core.entity;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.CheckListHolder;
import com.pixurvival.core.contentPack.effect.DelayedFollowingElement;
import com.pixurvival.core.contentPack.effect.Effect;
import com.pixurvival.core.contentPack.effect.EffectTarget;
import com.pixurvival.core.contentPack.effect.OffsetAngleEffect;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamMemberSerialization;
import com.pixurvival.core.util.ByteBufferUtils;
import com.pixurvival.core.util.VarLenNumberIO;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EffectEntity extends Entity implements CheckListHolder, TeamMember {

    private @Getter OffsetAngleEffect definition;

    private @Getter
    @Setter TeamMember ancestor;

    private @Getter
    @Setter Object movementData;

    private int nextFollowingElementIndex = 0;

    private Collection<Object> checkList;
    private Collection<Object> tmpCheckList;

    private int numberOfDelayedFollowingElements;

    private long creationTime;
    private long termTimeMillis;

    public EffectEntity(OffsetAngleEffect definition, TeamMember ancestor) {
        this.definition = definition;
        this.ancestor = ancestor;
    }

    public EffectEntity() {
        definition = new OffsetAngleEffect();
    }

    @Override
    public void initializeAtCreation() {
        if (getWorld().isServer()) {
            float angle = ancestor.getPosition().angleToward(ancestor.getTargetPosition()) + getDefinition().getOffsetAngle()
                    + getWorld().getRandom().nextAngle(getDefinition().getRandomAngle());
            definition.getEffect().getMovement().initialize(this, angle);
            List<DelayedFollowingElement> delayedFollowingElements = definition.getEffect().getDelayedFollowingElements();
            if (!delayedFollowingElements.isEmpty()) {
                creationTime = getWorld().getTime().getTimeMillis();
                int repeat = (int) definition.getEffect().getRepeatFollowingElements().getValue(this);
                numberOfDelayedFollowingElements = delayedFollowingElements.size() * repeat;
                termTimeMillis = Math.max(definition.getEffect().getDuration(), delayedFollowingElements.get(delayedFollowingElements.size() - 1).getDelay() * repeat)
                        + getWorld().getTime().getTimeMillis();
            } else {
                termTimeMillis = definition.getEffect().getDuration() + getWorld().getTime().getTimeMillis();
            }
        }
    }

    @Override
    public void update() {
        Effect effect = definition.getEffect();
        effect.getMovement().update(this);
        if (getWorld().getTime().getTimeMillis() >= termTimeMillis) {
            setAlive(false);
            setSneakyDeath(true);
        }
        if (getWorld().isServer()) {
            if (definition.getEffect().getTileCollisionAction() != null) {
                getWorld().getMap().forEachTile(getPosition().getX(), getPosition().getY(), effect.getMapCollisionRadius(), tile -> definition.getEffect().getTileCollisionAction().accept(this, tile));
            }
            if (definition.getEffect().getMovement().isDestroyWithAncestor() && !getAncestor().isAlive()) {
                setAlive(false);
            } else {
                processFollowingElements(getWorld().getTime().getTimeMillis() - creationTime);
                processEffectTarget();
            }
        } else if (definition.getEffect().isSolid()) {
            getWorld().getMap().forEachTile(getPosition().getX(), getPosition().getY(), effect.getMapCollisionRadius(), tile -> {
                if (tile.isSolid()) {
                    setAlive(false);
                    setSneakyDeath(true);
                }
            });
        }
        normalPositionUpdate();
        updateChunk();
    }

    private void processEffectTarget() {
        for (EffectTarget effectTarget : definition.getEffect().getTargets()) {
            EntitySearchUtils.forEach(this, effectTarget.getTargetType(), getCollisionRadius() + getWorld().getContentPack().getMaxLivingEntityRadius(), e -> {
                if (collideDynamic(e)) {
                    effectTarget.getAlterations().forEach(a -> a.apply(this, e));
                    if (effectTarget.isDestroyWhenCollide()) {
                        setAlive(false);
                    } else {
                        flushCheckList();
                    }
                }
            });
        }
    }

    private void processFollowingElements(long age) {
        Effect effect = definition.getEffect();
        if (nextFollowingElementIndex < numberOfDelayedFollowingElements) {
            DelayedFollowingElement nextDelayedFollowingElement;
            List<DelayedFollowingElement> followingElements = effect.getDelayedFollowingElements();
            int currentIndex = nextFollowingElementIndex % followingElements.size();
            int repeatCount = nextFollowingElementIndex / followingElements.size();
            long maxDelay = followingElements.get(followingElements.size() - 1).getDelay();
            while (nextFollowingElementIndex < numberOfDelayedFollowingElements && age >= (nextDelayedFollowingElement = followingElements.get(currentIndex)).getDelay() + repeatCount * maxDelay) {
                nextDelayedFollowingElement.getFollowingElement().apply(this);
                nextFollowingElementIndex++;
                currentIndex = nextFollowingElementIndex % followingElements.size();
                repeatCount = nextFollowingElementIndex / followingElements.size();
            }
        }
    }

    @Override
    protected void onDeath() {
        if (getWorld().isServer()) {
            TeamMember origin = getOrigin();
            for (Alteration alteration : definition.getEffect().getDeathAlterations()) {
                alteration.apply(this, origin);
            }
        }
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.EFFECT;
    }

    @Override
    public float getCollisionRadius() {
        return definition.getEffect().getEntityCollisionRadius();
    }

    @Override
    public void writeInitialization(ByteBuffer buffer) {
        VarLenNumberIO.writePositiveVarInt(buffer, definition.getEffect().getId());
    }

    @Override
    public void applyInitialization(ByteBuffer buffer) {
        definition.setEffect(getWorld().getContentPack().getEffects().get(VarLenNumberIO.readPositiveVarInt(buffer)));
    }

    @Override
    public void writeUpdate(ByteBuffer buffer, boolean full) {
        buffer.putFloat(getPosition().getX());
        buffer.putFloat(getPosition().getY());
        buffer.putFloat(getMovingAngle());
        buffer.put(isForward() ? (byte) 1 : (byte) 0);
        definition.getEffect().getMovement().writeUpdate(buffer, this);
        ByteBufferUtils.writeFutureTime(buffer, getWorld(), termTimeMillis);
    }

    @Override
    public void applyUpdate(ByteBuffer buffer) {
        getPosition().set(buffer.getFloat(), buffer.getFloat());
        setMovingAngle(buffer.getFloat());
        setForward(buffer.get() == 1);
        definition.getEffect().getMovement().applyUpdate(buffer, this);
        termTimeMillis = ByteBufferUtils.readFutureTime(buffer, getWorld());
    }

    @Override
    public void writeRepositoryUpdate(ByteBuffer buffer) {
        super.writeRepositoryUpdate(buffer);
        TeamMemberSerialization.write(buffer, ancestor, true);
        definition.getEffect().getMovement().writeRepositoryUpdate(buffer, this);
        if (!getDefinition().getEffect().getDelayedFollowingElements().isEmpty()) {
            ByteBufferUtils.writePastTime(buffer, getWorld(), creationTime);
            VarLenNumberIO.writePositiveVarInt(buffer, numberOfDelayedFollowingElements);
            VarLenNumberIO.writePositiveVarInt(buffer, nextFollowingElementIndex);
        }
    }

    @Override
    public void applyRepositoryUpdate(ByteBuffer buffer) {
        super.applyRepositoryUpdate(buffer);
        ancestor = TeamMemberSerialization.read(buffer, getWorld(), true);
        definition.getEffect().getMovement().applyRepositoryUpdate(buffer, this);
        if (!getDefinition().getEffect().getDelayedFollowingElements().isEmpty()) {
            creationTime = ByteBufferUtils.readPastTime(buffer, getWorld());
            numberOfDelayedFollowingElements = VarLenNumberIO.readPositiveVarInt(buffer);
            nextFollowingElementIndex = VarLenNumberIO.readPositiveVarInt(buffer);
        }
    }

    @Override
    public float getSpeedPotential() {
        return definition.getEffect().getMovement().getSpeedPotential(this);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean check(Object object) {
        if (checkList == null) {
            checkList = new ArrayList<>(3);
            tmpCheckList = new ArrayList<>(3);
        }
        if (checkList.contains(object)) {
            return true;
        } else {
            tmpCheckList.add(object);
            return false;
        }
    }

    @Override
    public Vector2 getTargetPosition() {
        return Vector2.fromEuclidean(1, getMovingAngle()).add(getPosition());
    }

    @Override
    public Team getTeam() {
        return ancestor.findIfNotFound().getTeam();
    }

    @Override
    public StatSet getStats() {
        return ancestor.findIfNotFound().getStats();
    }

    @Override
    public TeamMember getOrigin() {
        return ancestor.findIfNotFound().getOrigin().findIfNotFound();
    }

    @Override
    public boolean isInvisible() {
        return definition.getEffect().getSpriteSheet() == null;
    }

    @Override
    protected boolean antiCollisionLockEnabled() {
        return false;
    }

    @Override
    public void flushCheckList() {
        if (checkList != null) {
            checkList.addAll(tmpCheckList);
            tmpCheckList.clear();
        }
    }
}
