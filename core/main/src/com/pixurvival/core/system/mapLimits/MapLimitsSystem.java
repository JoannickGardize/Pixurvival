package com.pixurvival.core.system.mapLimits;

import com.pixurvival.core.ActionTimerManager;
import com.pixurvival.core.World;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.contentPack.gameMode.MapLimits;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.system.GameSystem;
import com.pixurvival.core.system.Inject;
import com.pixurvival.core.system.SystemData;
import com.pixurvival.core.system.interest.*;
import com.pixurvival.core.time.Time;
import com.pixurvival.core.util.Rectangle;
import com.pixurvival.core.util.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class MapLimitsSystem implements GameSystem, InitializeNewServerWorldInterest, InitializeNewClientWorldInterest, WorldUpdateInterest, PersistenceInterest {

    private @Getter MapLimitsSystemData data = new MapLimitsSystemData();

    @Inject
    private InterestSubscription<SystemDataChangedInterest> dataChangedInterestSubscription;

    @Inject
    private GameMode gameMode;

    @Inject
    private ActionTimerManager actionTimerManager;

    @Inject
    private Vector2 spawnCenter;

    @Inject
    private Time time;

    @Inject
    private EntityPool entityPool;

    @Override
    public boolean isRequired(World world) {
        return world.getGameMode().getMapLimits() != null;
    }

    @Override
    public void initializeNewServerWorld() {
        commonInitialization();
        List<MapLimitsAnchor> anchors = gameMode.getMapLimits().getAnchors();
        if (!anchors.isEmpty()) {
            for (int i = 1; i < anchors.size(); i++) {
                MapLimitsAnchor anchor = anchors.get(i);
                actionTimerManager.addActionTimer(new NextMapLimitAnchorAction(anchor), anchors.get(i - 1).getTime());
            }
        }
    }

    @Override
    public void initializeNewClientWorld(CreateWorld createWorld) {
        commonInitialization();
    }

    private void commonInitialization() {
        MapLimits mapLimits = gameMode.getMapLimits();
        data.setRectangle(new Rectangle(spawnCenter, mapLimits.getInitialSize()));
        data.setTrueDamagePerSecond(mapLimits.getInitialDamagePerSecond());
        MapLimitsAnchorRun initialAnchorRun = new MapLimitsAnchorRun();
        initialAnchorRun.setDamagePerSecond(mapLimits.getInitialDamagePerSecond());
        initialAnchorRun.setRectangle(data.getRectangle());
        initialAnchorRun.setTime(0);
        data.setFrom(initialAnchorRun);
        List<MapLimitsAnchor> anchors = mapLimits.getAnchors();
        data.setTo(initialAnchorRun);
        if (!anchors.isEmpty()) {
            actionTimerManager.addActionTimer(new NextMapLimitAnchorAction(anchors.get(0)), 0);
        }
    }

    @Override
    public void update(float deltaTime) {
        MapLimitsAnchorRun from = data.getFrom();
        MapLimitsAnchorRun to = data.getTo();
        long diffTime = to.getTime() - from.getTime();
        Rectangle rectangle = data.getRectangle();
        if (diffTime > 0) {
            float alpha = Math.min(1, (float) ((double) (time.getTimeMillis() - from.getTime()) / diffTime));
            Rectangle fromRect = from.getRectangle();
            Rectangle toRect = to.getRectangle();
            rectangle.setStartX(fromRect.getStartX() + (toRect.getStartX() - fromRect.getStartX()) * alpha);
            rectangle.setStartY(fromRect.getStartY() + (toRect.getStartY() - fromRect.getStartY()) * alpha);
            rectangle.setEndX(fromRect.getEndX() + (toRect.getEndX() - fromRect.getEndX()) * alpha);
            rectangle.setEndY(fromRect.getEndY() + (toRect.getEndY() - fromRect.getEndY()) * alpha);
            data.setTrueDamagePerSecond(from.getDamagePerSecond() + (to.getDamagePerSecond() - from.getDamagePerSecond()) * alpha);
        }
        for (Entity e : entityPool.get(EntityGroup.PLAYER)) {
            if (!rectangle.contains(e.getPosition())) {
                ((LivingEntity) e).takeTrueDamageSneaky(data.getTrueDamagePerSecond() * deltaTime, DamageAttributes.getDefaults());
            }
        }
    }

    @Override
    public void setData(Object data) {
        this.data = (MapLimitsSystemData) data;
    }

    @Override
    public void accept(SystemData data) {
        setData(data);
    }

    void notifyAnchorChanged() {
        dataChangedInterestSubscription.forEach(i -> i.dataChanged(data));
    }
}
