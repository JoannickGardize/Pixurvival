package com.pixurvival.core.system.mapLimits;

import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.alteration.DamageAttributes;
import com.pixurvival.core.contentPack.gameMode.MapLimits;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.system.BaseSystem;
import com.pixurvival.core.system.SystemData;
import com.pixurvival.core.system.interest.InitializeNewClientWorldInterest;
import com.pixurvival.core.system.interest.InitializeNewServerWorldInterest;
import com.pixurvival.core.system.interest.InterestSubscription;
import com.pixurvival.core.system.interest.PersistenceInterest;
import com.pixurvival.core.system.interest.WorldUpdateInterest;
import com.pixurvival.core.util.Rectangle;

import lombok.Getter;

@Getter
public class MapLimitsSystem extends BaseSystem implements InitializeNewServerWorldInterest, InitializeNewClientWorldInterest, WorldUpdateInterest, PersistenceInterest {

	private MapLimitsSystemData data = new MapLimitsSystemData();

	private InterestSubscription<MapLimitsAnchorInterest> mapLimitsAnchorSubscription;

	public MapLimitsSystem(World world) {
		super(world);
		mapLimitsAnchorSubscription = world.getInterestSubscriptionSet().get(MapLimitsAnchorInterest.class);
	}

	@Override
	public boolean isRequired() {
		return getWorld().getGameMode().getMapLimits() != null;
	}

	@Override
	public void initializeNewServerWorld() {
		commonInitialization();
		List<MapLimitsAnchor> anchors = getWorld().getGameMode().getMapLimits().getAnchors();
		if (!anchors.isEmpty()) {
			for (int i = 1; i < anchors.size(); i++) {
				MapLimitsAnchor anchor = anchors.get(i);
				getWorld().getActionTimerManager().addActionTimer(new NextMapLimitAnchorAction(anchor), anchors.get(i - 1).getTime());
			}
		}
	}

	@Override
	public void initializeNewClientWorld(CreateWorld createWorld) {
		commonInitialization();
	}

	private void commonInitialization() {
		MapLimits mapLimits = getWorld().getGameMode().getMapLimits();
		data.setRectangle(new Rectangle(getWorld().getSpawnCenter(), mapLimits.getInitialSize()));
		data.setTrueDamagePerSecond(mapLimits.getInitialDamagePerSecond());
		MapLimitsAnchorRun initialAnchorRun = new MapLimitsAnchorRun();
		initialAnchorRun.setDamagePerSecond(mapLimits.getInitialDamagePerSecond());
		initialAnchorRun.setRectangle(data.getRectangle());
		initialAnchorRun.setTime(0);
		data.setFrom(initialAnchorRun);
		List<MapLimitsAnchor> anchors = mapLimits.getAnchors();
		data.setTo(initialAnchorRun);
		if (!anchors.isEmpty()) {
			new NextMapLimitAnchorAction(anchors.get(0)).perform(getWorld());
		}
	}

	@Override
	public void update(float deltaTime) {
		MapLimitsAnchorRun from = data.getFrom();
		MapLimitsAnchorRun to = data.getTo();
		long diffTime = to.getTime() - from.getTime();
		Rectangle rectangle = data.getRectangle();
		if (diffTime > 0) {
			long time = getWorld().getTime().getTimeMillis();
			float alpha = Math.min(1, (float) ((double) (time - from.getTime()) / diffTime));
			Rectangle fromRect = from.getRectangle();
			Rectangle toRect = to.getRectangle();
			rectangle.setStartX(fromRect.getStartX() + (toRect.getStartX() - fromRect.getStartX()) * alpha);
			rectangle.setStartY(fromRect.getStartY() + (toRect.getStartY() - fromRect.getStartY()) * alpha);
			rectangle.setEndX(fromRect.getEndX() + (toRect.getEndX() - fromRect.getEndX()) * alpha);
			rectangle.setEndY(fromRect.getEndY() + (toRect.getEndY() - fromRect.getEndY()) * alpha);
			data.setTrueDamagePerSecond(from.getDamagePerSecond() + (to.getDamagePerSecond() - from.getDamagePerSecond()) * alpha);
		}
		for (Entity e : getWorld().getEntityPool().get(EntityGroup.PLAYER)) {
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

	// TODO generic data changed
	void notifyAnchorChanged() {
		mapLimitsAnchorSubscription.forEach(i -> i.anchorChanged(data));
	}
}
